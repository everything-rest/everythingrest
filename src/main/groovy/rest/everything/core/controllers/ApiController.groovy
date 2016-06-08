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
            QueryBuilder qb = buildQuery(request.getParameterMap())
            SearchResponse sResponse = ContextHolder.instance.transportClient.prepareSearch(collection)
                    .setTypes(type)
                    .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                    .setQuery(qb)
                    .setFrom(_from).setSize(100)
                    .execute().actionGet();
            return sResponse.getHits().collect { [id:it.id, data: it.source, score: it.score] }
        }catch (Exception e){

        }
    }

    private QueryBuilder buildQuery(Map<String,String[]> query){
        QueryBuilder qb = QueryBuilders.boolQuery()
        query.findAll{ !it.key.startsWith('_') }each {
            qb = qb.must(QueryBuilders.queryStringQuery(it.value[0]).defaultField(it.key))
        }
        return qb
    }


    @RequestMapping(value="/api/{collection}/{type}/{id}", produces = "application/json")
    @ResponseBody public Map get(@PathVariable String collection, @PathVariable String type, @PathVariable String id, HttpServletRequest request,HttpServletResponse response){
        try {
            GetResponse gr = ContextHolder.instance.transportClient.prepareGet(collection, type, id).get();
            return gr.getSourceAsMap();
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }


    @RequestMapping(value="/api", produces = "application/json")
    @ResponseBody public String[] listCollections( HttpServletRequest request){
        return ContextHolder.instance.transportClient.admin().cluster()
                .prepareState().execute()
                .actionGet().getState()
                .getMetaData().getConcreteAllIndices();
    }
    @RequestMapping(value="/api/{collection}", produces = "application/json")
    public def listTypes(@PathVariable String collection, HttpServletRequest request, HttpServletResponse response) throws ExecutionException, InterruptedException {
        try {
            GetMappingsResponse res = ContextHolder.instance.transportClient.admin().indices().getMappings(new GetMappingsRequest().indices(collection)).get();
            ImmutableOpenMap<String, MappingMetaData> mapping = res.mappings().get(collection);
            return mapping.collect { it.key }
        }catch(Exception ex){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
