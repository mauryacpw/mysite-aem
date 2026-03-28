    package com.mysite.core.models;

    import com.fasterxml.jackson.core.JsonProcessingException;
    import com.fasterxml.jackson.databind.ObjectMapper;
    import com.mysite.core.service.QuoteService;
    import org.apache.sling.api.resource.Resource;
    import org.apache.sling.models.annotations.DefaultInjectionStrategy;
    import org.apache.sling.models.annotations.Model;
    import org.apache.sling.models.annotations.injectorspecific.OSGiService;

    import javax.annotation.PostConstruct;
    import java.util.ArrayList;
    import java.util.List;

    @Model(
            adaptables = Resource.class,
            adapters = QuoteModel.class,
            defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
    )
    public class QuoteModelImpl implements QuoteModel {

        @OSGiService
        QuoteService quoteService;

        private List quotes = new ArrayList<>();


        @PostConstruct
        protected void init() {
            if (quoteService != null) {
                try {
                    String jsonResponse;
                    jsonResponse = quoteService.getResponse();

                    ObjectMapper objectMapper = new ObjectMapper();
                    quotes =  objectMapper.readValue(jsonResponse,List.class);

                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        public List<Object> getQuotes() {
            return quotes;
        }
        void setQuoteService(QuoteService quoteService) {
            this.quoteService = quoteService;
        }
    }