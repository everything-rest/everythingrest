package rest.everything.core
/**
 *
 */
class InsertData {

    def configuration
    def data

    public InsertData(def data,def configuration){
        this.data = data
        this.configuration = configuration
    }
    public void go(){
        data.eachWithIndex { it,index->
            String indexValue = index
            if(configuration.id_field)
                indexValue = it[configuration.id_field]
            ContextHolder.instance.transportClient
                    .prepareIndex(configuration.collection,configuration.type)
                    .setId(indexValue)
                    .setSource(it).get()
        }
    }
}
