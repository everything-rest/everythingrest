package rest.everything.core.data

import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse
import org.elasticsearch.action.get.GetResponse
import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.cluster.metadata.MappingMetaData
import org.elasticsearch.common.collect.ImmutableOpenMap
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.index.query.QueryBuilders
import rest.everything.core.ContextHolder

/**
 *
 */
class ElasticData {

    public static def get(String collection,String type,String id){
        GetResponse gr = ContextHolder.instance.transportClient.prepareGet(collection, type, id).get();
        return gr.getSourceAsMap();
    }

    public static QueryBuilder buildQuery(Map<String,String[]> query){
        QueryBuilder qb = QueryBuilders.boolQuery()
        query.findAll{ !it.key.startsWith('_') }each {
            qb = qb.must(QueryBuilders.queryStringQuery(it.value[0]).defaultField(it.key))
        }
        return qb
    }

    public static def search(String collection,String type, int _from, def paramsMap){
        QueryBuilder qb = buildQuery(paramsMap)
        SearchResponse sResponse = ContextHolder.instance.transportClient.prepareSearch(collection)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(qb)
                .setFrom(_from).setSize(100)
                .execute().actionGet();
        return sResponse.getHits().collect { [id:it.id, data: it.source, score: it.score] }
    }

    public static def listCollections(){
        return ContextHolder.instance.transportClient.admin().cluster()
                .prepareState().execute()
                .actionGet().getState()
                .getMetaData().getConcreteAllIndices();
    }

    public static def listTypes(String collection){
        GetMappingsResponse res = ContextHolder.instance.transportClient.admin().indices().getMappings(new GetMappingsRequest().indices(collection)).get();
        ImmutableOpenMap<String, MappingMetaData> mapping = res.mappings().get(collection);
        return mapping.collect { it.key }
    }

    public static def getScript(String signature){
        return get('runnable','groovy',signature)
    }


}
