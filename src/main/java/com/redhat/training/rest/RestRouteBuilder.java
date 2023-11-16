package com.redhat.training.rest;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;
@Component
public class RestRouteBuilder extends RouteBuilder {

    private JacksonDataFormat jacksonDataFormat = new JacksonDataFormat(Client.class);

    @Override
    public void configure() throws Exception {
        System.out.println("hi 1");

        // configure rest-dsl
        restConfiguration()
           	// to use servlet component and run on port 8080
            .component("servlet")
			.port(8080)
            .contextPath("/camel")
			.bindingMode(RestBindingMode.json);
        System.out.println("hi 2");

            // Route for creating a client
            rest("/client")
                .post("/create")
                .to("direct:sendtoKafka")
                .to("direct:createClient");

            // Route for getting a client
            rest("/client")
                .get("/find/{id}")
                .to("direct:getClient");

            // Route for deleting a client
            rest("/client")
                .delete("/delete/{id}")
                .to("direct:deleteClient");

            from("direct:sendtoKafka")
                .log("Received request to create a client 1: ")   
                // Add your processing logic here
                .setBody(constant("Message from Camel"))
                .to("kafka:messages");


            // Direct routes for processing each operation
            from("direct:createClient")
               .process(exchange -> {
                String message = exchange.getIn().getBody(String.class);
                System.out.println("message 2: "+message);
                })
                .log("Received request to create a client: 2 ")   
                // Add your processing logic here
                .setBody(constant("{'status':'client entered successfully'}"));

            from("direct:getClient")
                .log("Received request to get a client")
                // Add your processing logic here
                .setBody(constant("{'status':'get client response'}"));

            from("direct:deleteClient")
                .log("Received request to delete a client")
                // Add your processing logic here
                .setBody(constant("{'status':'delete response'}"));   }
}
