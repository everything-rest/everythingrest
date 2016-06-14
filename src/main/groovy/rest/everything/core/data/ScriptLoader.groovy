package rest.everything.core.data

import javax.servlet.http.HttpServletRequest

/**
 *
 */
class ScriptLoader {

    private GroovyShell groovyShell

    private static ScriptLoader instance

    private ScriptLoader(){
        groovyShell = new GroovyShell()
    }
    public static ScriptLoader getInstance(){
        if (instance == null)
            instance = new ScriptLoader()
        return instance
    }

    public Script parse(String text,String signature){
        Class aClass
        try {
            aClass = groovyShell.classLoader.loadClass(signature)
        }catch(Exception e){
            aClass = groovyShell.classLoader.parseClass(text,signature)
        }
        return aClass.newInstance()
    }

    public static String generateSignature(String collection,String type,String name){
        return collection+'_'+type+'_'+name
    }

    public static def toBinding(def scriptMap, def reqMap){
        Binding binding = new Binding()
        scriptMap.each {
            String name = it.name
            def val = reqMap[name]
            if(val)
                val = val[0]
            binding.setVariable(name,val)
        }
        return binding
    }

    public def run(String text, String signature, def scriptMap, HttpServletRequest request){
        def reqMap = request.parameterMap
        Script script = parse(text,signature)
        if(script) {
            Binding binding = toBinding(scriptMap, reqMap)
            binding.setVariable('req_address',request.remoteAddr)
            script.setBinding(binding)
            return script.run()
        }
        return null
    }
}
