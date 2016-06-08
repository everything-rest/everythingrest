package rest.everything.core

import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import rest.everything.core.controllers.JsonCallbackFilter
/**
 *
 */
@Configuration
class ConfigBean {


    JsonCallbackFilter jsonpFilter = new JsonCallbackFilter();

    @Bean
    public FilterRegistrationBean jsonpFilter() {
        FilterRegistrationBean filterRegBean = new FilterRegistrationBean();
        filterRegBean.setFilter(jsonpFilter);
        return filterRegBean;
    }
}
