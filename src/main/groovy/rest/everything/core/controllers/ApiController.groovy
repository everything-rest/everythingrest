package rest.everything.core.controllers

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.cluster.metadata.MappingMetaData
import org.elasticsearch.common.collect.ImmutableOpenMap
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import rest.everything.core.ContextHolder
import rest.everything.core.data.ElasticData
import rest.everything.core.data.ScriptLoader

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.concurrent.ExecutionException
/**
 *
 */
@Component
@RestController
public class ApiController {

    @RequestMapping(value="/api/{collection}/{type}", produces = "application/json",method=RequestMethod.GET)
    @ResponseBody public List exactSearch(@PathVariable String collection, @PathVariable String type, @RequestParam(defaultValue = "0") int _from, HttpServletRequest request, HttpServletResponse response){
        try {
            return ElasticData.search(collection,type,_from,request.getParameterMap())
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value="/api/{collection}/{type}/{id}", produces = "application/json")
    @ResponseBody public Map get(@PathVariable String collection, @PathVariable String type, @PathVariable String id, HttpServletRequest request,HttpServletResponse response){
        try {
            return ElasticData.get(collection,type,id)
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    @RequestMapping(value="/api/{collection}/{type}/{id}/run", produces = "application/json")
    @ResponseBody public Map run(@PathVariable String collection, @PathVariable String type, @PathVariable String id, HttpServletRequest request,HttpServletResponse response){
        try {
            GetResponse gr = ContextHolder.instance.transportClient.prepareGet(collection, type, id).get();
            def map = gr.getSourceAsMap();
            if(map.runnable == true){
                String signature = ScriptLoader.generateSignature(collection,type,id)
                def scriptObject = ElasticData.getScript(signature)
                def result = ScriptLoader.instance.run(scriptObject.script,signature,map.run.parameters,request.parameterMap)
                def res = [result:result]
                return res
            }
        }catch (Exception e){
            e.printStackTrace()
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @RequestMapping(value="/api", produces = "application/json")
    @ResponseBody public String[] listCollections(HttpServletRequest request){
        return ElasticData.listCollections()
    }
    @RequestMapping(value="/api/{collection}", produces = "application/json")
    public def listTypes(@PathVariable String collection, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException {
        try {
            return ElasticData.listTypes(collection)
        }catch(Exception ex){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
