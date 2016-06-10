package rest.everything.core.importer

import rest.everything.core.ContextHolder
import rest.everything.core.data.ScriptLoader

/**
 *
 */
class InsertScript {

    File file
    def configuration

    public InsertScript(File file,def configuration){
        this.file = file
        this.configuration = configuration
    }

    public void go(){
        String script = file.getText()
        String name = file.name.substring(0,file.name.lastIndexOf('.'))
        String signature = ScriptLoader.generateSignature(configuration.collection,configuration.type,name)
        def data = [script:script,signature:signature,name:name]
        ContextHolder.instance.transportClient
                .prepareIndex('runnable','groovy')
                .setId(signature)
                .setSource(data).get()
    }
}
