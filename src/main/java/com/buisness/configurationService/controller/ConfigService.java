package com.buisness.configurationService.controller;

import java.io.IOException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.buisness.configurationService.service.DroolsClient;
import com.sdlc.configurationService.model.Client;
import com.sdlc.servicerulerepo.ConfigurableClientTemplate;
import com.sdlc.templaterule.ClientTemplate;

@RestController
public class ConfigService {

	@Autowired
	DroolsClient droolsClient;
	
	/*
	 * @GetMapping("clientTemplate/client/{clientName}") public ClientTemplate
	 * getCreditscore(@PathVariable String clientName) { ClientTemplate
	 * clientTemplate = new ClientTemplate();
	 * clientTemplate.setClientName(clientName);;
	 * 
	 * return droolsClient.executeCommands(clientTemplate); }
	 */
	
	 @GetMapping(value="/service/v3/getClientTemplate/client/{client}",produces = "application/json")
	   	public Object getClientTemplateFromDrools(@PathVariable("client") Client client ) throws IOException {
	   	
	       
	    	ConfigurableClientTemplate clientTemplate = new ConfigurableClientTemplate();
		 	clientTemplate.setClientID(client.name());
		 	
		 	return  droolsClient.executeCommands(clientTemplate);
		 	
	  	}
	

}
