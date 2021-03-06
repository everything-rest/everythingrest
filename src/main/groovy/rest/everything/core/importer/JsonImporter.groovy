package rest.everything.core.importer

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import rest.everything.core.InsertData
import rest.everything.core.UpdateIndex
import org.apache.commons.io.FileUtils

class JsonImporter {

    public static void main(def args){
        String path = args[0]
        if(path == null){
            println 'You need to provide the path of the project to import'
            return
        }
        File projectFile = new File(path)
        if(!projectFile.exists()){
            println 'The provided path leads nowhere...'
            return
        }
        File config = FileUtils.getFile(projectFile,'config.json')
        if(!config.exists()){
            println 'The configuration file does not exist'
            return
        }
        File dataDirectory = FileUtils.getFile(projectFile,'data')
        File scriptDirectory = FileUtils.getFile(projectFile,'scripts')
        if(!dataDirectory.exists()){
            println 'The data directory does not exist'
            return
        }
        def configData
        try {
            configData = new JsonSlurper().parse(config)
        }catch(Exception e){
            println 'The configuration file doesn\'t look like valid JSON'
            return
        }
        new UpdateIndex(configData).go()
        updateData(configData,dataDirectory)
        updateScripts(configData,scriptDirectory)

    }

    private static void updateData(def configData,File dataDirectory){
        def items = new LinkedList()
        def files = dataDirectory.listFiles()
        files = files.sort{it1,it2 -> it1.name.compareTo(it2.name) }
        def iterator = files.iterator()
        while(iterator.hasNext()){
            def it = iterator.next()
            if (it.name.endsWith('.json')) try{
                def dataFile = new JsonSlurper().parse(it)
                items.addAll(dataFile)
            }catch(Exception e){
                println 'File '+it.name+' doesn\'t look like valid JSON'
                return
            }
        }
        new InsertData(items,configData).go()
    }

    private static void updateScripts(def configData,File scriptDirectory){
        def files = scriptDirectory.listFiles()
        def iterator = files.iterator()
        while(iterator.hasNext()){
            def it = iterator.next()
            if (it.name.endsWith('.groovy')) try{
                new InsertScript(it,configData).go()
            }catch(Exception e){e.printStackTrace()}
        }
    }
}
