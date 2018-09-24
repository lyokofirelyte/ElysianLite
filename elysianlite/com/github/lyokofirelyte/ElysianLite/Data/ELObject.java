package com.github.lyokofirelyte.ElysianLite.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;

public class ELObject extends JSONObject {

	private static final long serialVersionUID = 1L;

	public ELObject(){
		defaults();
	}
	
	public ELObject(JSONObject o){
		defaults();
		for (Object key : o.keySet()){
			put(key, o.get(key));
		}
	}
	
	@SuppressWarnings("unchecked")
	private void defaults(){
		put(ELData.PERMS.getName(), new ArrayList<String>(Arrays.asList(
			"wub.lub",
			"wub.rank.0"
		)));
	}
}