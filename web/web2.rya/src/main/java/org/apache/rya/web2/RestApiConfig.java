package org.apache.rya.web2;

import org.apache.commons.collections.list.PredicatedList;
import org.apache.rya.web2.converters.SparqlJsonMessageConverter;
import org.apache.rya.web2.converters.SparqlStarJsonMessageConverter;
import org.apache.rya.web2.converters.SparqlStarXmlMessageConverter;
import org.apache.rya.web2.converters.SparqlXmlMessageConverter;
import org.eclipse.rdf4j.query.resultio.TupleQueryResultFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.accept.FixedContentNegotiationStrategy;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.servlet.config.annotation.*;

import java.util.*;

@Configuration
public class RestApiConfig implements WebMvcConfigurer {

    @Value("${cors.allowedOrigins}")
    private String allowedOrigins;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/web/**").addResourceLocations("classpath:/public/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("*")
                .allowCredentials(true);
    }

    static final Map<String, MediaType> mediaTypes = new HashMap<>();
    static {
        mediaTypes.put(TupleQueryResultFormat.SPARQL.getDefaultMIMEType(), MediaType.valueOf(TupleQueryResultFormat.SPARQL.getDefaultMIMEType()));
        mediaTypes.put(TupleQueryResultFormat.SPARQL_STAR.getDefaultMIMEType(), MediaType.valueOf(TupleQueryResultFormat.SPARQL_STAR.getDefaultMIMEType()));
        mediaTypes.put(TupleQueryResultFormat.JSON.getDefaultMIMEType(), MediaType.valueOf(TupleQueryResultFormat.JSON.getDefaultMIMEType()));
        mediaTypes.put(TupleQueryResultFormat.JSON_STAR.getDefaultMIMEType(), MediaType.valueOf(TupleQueryResultFormat.JSON_STAR.getDefaultMIMEType()));
        mediaTypes.put("application/json", MediaType.valueOf("application/json"));
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.mediaTypes(mediaTypes)
                  .strategies(Collections.singletonList(new HeaderContentNegotiationStrategy()));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new SparqlJsonMessageConverter());
        converters.add(new SparqlStarJsonMessageConverter());
        converters.add(new SparqlXmlMessageConverter());
        converters.add(new SparqlStarXmlMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
