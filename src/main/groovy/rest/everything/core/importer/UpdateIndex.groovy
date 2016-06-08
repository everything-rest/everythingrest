package rest.everything.core
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse
/**
 *
 */
class UpdateIndex {

    def configuration

    public UpdateIndex(def configuration){
        this.configuration = configuration

    }

    public void go(){
        checkOrCreateIndex()
    }

    private void checkOrCreateIndex(){
        def mapping = [:]
        mapping.properties = configuration.mapping
        IndicesExistsResponse response = ContextHolder.instance.transportClient.admin().indices().prepareExists(configuration.collection).get()
        if(!response.exists){
            def op = ContextHolder.instance.transportClient.admin().indices().prepareCreate(configuration.collection)
            if(configuration.mapping)
                op = op.addMapping(configuration.type,mapping)
            op.get()
        }

    }
}
