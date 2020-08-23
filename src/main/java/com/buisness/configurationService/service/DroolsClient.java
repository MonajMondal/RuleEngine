package com.buisness.configurationService.service;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.command.KieCommands;
import org.kie.api.runtime.ExecutionResults;
import org.kie.internal.command.CommandFactory;
import org.kie.server.api.marshalling.MarshallingFormat;
import org.kie.server.api.model.*;
import org.kie.server.client.KieServicesClient;
import org.kie.server.client.KieServicesConfiguration;
import org.kie.server.client.KieServicesFactory;
import org.kie.server.client.RuleServicesClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sdlc.servicerulerepo.ConfigurableClientTemplate;
import com.sdlc.templaterule.ClientTemplate;

@Service
public class DroolsClient {

	@Value("${KIE_USERNAME}")
	private String USER;
	
	@Value("${KIE_PASSWORD}")
    private String PASSWORD;

	@Value("${KIE_URL}")	private String URL;

	@Value("${CONTAINER}")
	private String CONTAINER;
	
	private static final MarshallingFormat FORMAT = MarshallingFormat.JSON;

    private KieServicesConfiguration conf;
    private KieServicesClient kieServicesClient;

    public void initialize() {
        conf = KieServicesFactory.newRestConfiguration(URL, USER, PASSWORD);
        conf.setMarshallingFormat(FORMAT);
        kieServicesClient = KieServicesFactory.newKieServicesClient(conf);
    }

    public void listCapabilities() {
        KieServerInfo serverInfo = kieServicesClient.getServerInfo().getResult();
        System.out.print("Server capabilities:");
        for(String capability: serverInfo.getCapabilities()) {
            System.out.print(" " + capability);
        }
        System.out.println();
    }


    public void listContainers() {
        KieContainerResourceList containersList = kieServicesClient.listContainers().getResult();
        List<KieContainerResource> kieContainers = containersList.getContainers();
        System.out.println("Available containers: ");
        for (KieContainerResource container : kieContainers) {
            System.out.println("\t" + container.getContainerId() + " (" + container.getReleaseId() + ")");
        }
    }
    
    public ConfigurableClientTemplate executeCommands(ConfigurableClientTemplate clientTemplate) {
    	if(kieServicesClient ==  null)initialize();
    	
        List cmds = new ArrayList();

        cmds.add(CommandFactory.newInsert( clientTemplate, CONTAINER ) );
        cmds.add(CommandFactory.newFireAllRules());


        System.out.println("== Sending commands to the server ==");
        RuleServicesClient rulesClient = kieServicesClient.getServicesClient(RuleServicesClient.class);

        KieCommands commandsFactory = KieServices.Factory.get().getCommands();
        Command<?> batchCommand = commandsFactory.newBatchExecution(cmds);

        ServiceResponse<ExecutionResults> executeResponse = rulesClient.executeCommandsWithResults(CONTAINER, batchCommand);
        
        ConfigurableClientTemplate clientTemplateResponse = null;
        if(executeResponse.getType() == KieServiceResponse.ResponseType.SUCCESS) {
            System.out.println("Commands executed with success! Response: " + executeResponse.getResult());
            clientTemplateResponse = (ConfigurableClientTemplate) executeResponse.getResult().getValue(CONTAINER);
            System.out.println("clientTemplateResponse = "+clientTemplateResponse);
        }
        else {
            System.out.println("Error executing rules. Message: ");
            System.out.println(executeResponse.getMsg());
        }
        
        return clientTemplateResponse;
    }
	
}


