package rest.everything.core.controllers

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.catalina.core.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import rest.everything.core.importer.JsonImporter
/**
 *
 */
@Component
@RestController
class AdminController {


    @RequestMapping(value="/admin/doIndex", produces = "application/json",method=RequestMethod.GET)
    @ResponseBody public def index(@RequestParam String path){
        String[] params = new String[1]
        params[0] = '/opt/projects/'+path
        JsonImporter.main(params)
        return [status:'accepted']
    }

}
