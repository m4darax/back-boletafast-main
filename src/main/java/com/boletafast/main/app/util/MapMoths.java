package com.boletafast.main.app.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class MapMoths {

	private final Map<String, String> MESES = new HashMap<String, String>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{
        put("january", "Enero");
        put("february", "Febrero");
        put("march", "Marzo");
        put("april", "Abril");
        put("may", "Mayo");
        put("june", "Junio");
        put("july", "Julio");
        put("august", "Agosto");
        put("september", "Septiembre");
        put("october", "Octubre");
        put("november", "Noviembre");
        put("december", "Diciembre");
    }};
	
	
	
	public String getMoth(String moth) {
		return this.MESES.get(moth);
	}

}
