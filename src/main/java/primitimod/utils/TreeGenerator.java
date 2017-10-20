package primitimod.utils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import primitimod.PrimitiMod;

public class TreeGenerator {
	
	private static Path assetsPath = Paths.get(".","src","main","resources","assets", PrimitiMod.MODID).toAbsolutePath().normalize();
	private static Path blockstateTreesPath = Paths.get(assetsPath.toString(),"blockstates","trees").toAbsolutePath().normalize();
	private static Path modelsBlockTreesPath = Paths.get(assetsPath.toString(),"models","block","trees").toAbsolutePath().normalize();
	private static Path texturesBlocksTreesPath = Paths.get(assetsPath.toString(),"textures","blocks","trees").toAbsolutePath().normalize();
	
	private static Path mainJavaPath = Paths.get(".","src","main","java", PrimitiMod.MODID).toAbsolutePath().normalize();
	private static Path treesTEPath = Paths.get(mainJavaPath.toString(),"trees","tileentity").toAbsolutePath().normalize();
	private static Path corePath = Paths.get(mainJavaPath.toString(),"core").toAbsolutePath().normalize();
	
	private static String registerBlockTag = "/*#TreeGenerator_registerBlocks*/";
	private static String registerItemTag = "/*#TreeGenerator_registerItems*/";
	private static String registerItemBlockTag = "/*#TreeGenerator_registerItemBlocks*/";
	private static String registrarTag = "/*#TreeGenerator_registrar*/";
	private static String importTETag = "/*#TreeGenerator_importTE*/";
	private static String registerColourTag = "/*#TreeGenerator_registerColour*/";
	private static String initModelsTag = "/*#TreeGenerator_initItemModels*/";
	
	
	public static void main(String[] args) {
		System.out.println("TreeGenerator start "+Paths.get(".","assets").toAbsolutePath().normalize().toString());
		
		String newTreeName = args.length > 0 ? args[0].toLowerCase() : "nullarg";
		String newTreeNameU = newTreeName.substring(0, 1).toUpperCase() + newTreeName.substring(1);
		
		String modBlocks = "PrimitiModBlocks";
		
		if(Files.isDirectory(Paths.get(blockstateTreesPath.toString(), newTreeName))) {
			System.out.println(Paths.get(blockstateTreesPath.toString(), newTreeName).toString() + " already exists!");
		}
		else if(Files.isDirectory(Paths.get(modelsBlockTreesPath.toString(), newTreeName))) {
			System.out.println(Paths.get(modelsBlockTreesPath.toString(), newTreeName).toString() + " already exists!");
		}
		else {
			try {
				Path newBlockstatePath = Files.createDirectory(Paths.get(blockstateTreesPath.toString(), newTreeName));
				makeJsonFiles(newBlockstatePath, getBlockstateFilesContent(), PrimitiMod.MODID, newTreeName);
			}
			catch(IOException e) { e.printStackTrace(); }
			
			try {
				Path newModelPath = Files.createDirectory(Paths.get(modelsBlockTreesPath.toString(), newTreeName));
				makeJsonFiles(newModelPath, getModelFilesContent(newTreeName), PrimitiMod.MODID, newTreeName);
			}
			catch(IOException e) { e.printStackTrace(); }

			try {
				Path newTexturePath = Files.createDirectories(Paths.get(texturesBlocksTreesPath.toString(), newTreeName));
				copyDefaultTextures(newTexturePath, Paths.get(texturesBlocksTreesPath.toString(), "_default"), newTreeName);
			}
			catch(IOException e) { e.printStackTrace(); }
			
			
			try {
				String teFilename = newTreeNameU+"TreeRootTE.java";
				Path newTEPath = Files.createFile(Paths.get(treesTEPath.toString(), teFilename));
				Files.write(newTEPath, getNewTreeTECode(PrimitiMod.MODID, modBlocks, newTreeNameU).getBytes());
			}
			catch(IOException e) { e.printStackTrace(); }
			
			try {
				Path path = Paths.get(corePath.toString(), modBlocks+".java");
				String content = new String(Files.readAllBytes(path));
				content = content.replace(registrarTag, getRegistrarSubclassCode(newTreeName, newTreeNameU));
				Files.write(path, content.getBytes());
			}
			catch(IOException e) { e.printStackTrace(); }
			
			try {
				Path path = Paths.get(corePath.toString(),"CommonProxy.java");
				String content = new String(Files.readAllBytes(path));
				content = content.replace(registerBlockTag, getBlockRegisterCode(modBlocks+"."+newTreeNameU, newTreeNameU))
								 .replace(registerItemTag, getItemRegisterCode(modBlocks+"."+newTreeNameU))
							 	 .replace(registerItemBlockTag, getItemBlockRegisterCode(modBlocks+"."+newTreeNameU))
							 	 .replace(importTETag, getImportTECode(newTreeNameU, PrimitiMod.MODID));;
				Files.write(path, content.getBytes());
			}
			catch(IOException e) { e.printStackTrace(); }
			
			try {
				Path path = Paths.get(corePath.toString(), "client", "ClientProxy.java");
				String content = new String(Files.readAllBytes(path));
				content = content.replace(initModelsTag, getInitModelsCode(modBlocks+"."+newTreeNameU))
								 .replace(registerColourTag, getColourRegisterCode(modBlocks+"."+newTreeNameU));
				Files.write(path, content.getBytes());
			}
			catch(IOException e) { e.printStackTrace(); }
		}
		
		
		
	}
	private static void makeJsonFiles(Path newBlockstatePath, Map<String, String> jsonFileContents, String modid, String treeName) {
		final String jsonExt = ".json";

		for(String filename : jsonFileContents.keySet()) {
			try {
				Path jsonFile = Files.createFile(Paths.get(newBlockstatePath.toString(), filename+jsonExt));
				Files.write(jsonFile, jsonFileContents.get(filename).replace("##modid##", modid).replace("##name##", treeName).getBytes());
			}
			catch(IOException e) {}
		}
		
	}
	
	private static void copyDefaultTextures(Path newTexturePath, Path defaultTexturePath, String treeName) {
		
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(defaultTexturePath)) {
            for (Path filepath : directoryStream) {
                Files.copy(filepath, Paths.get(newTexturePath.toString(), filepath.getFileName().toString().replace("default", treeName)));
            }
        } catch (IOException ex) {}
		
	}
	
	private static Map<String, String> getBlockstateFilesContent() {
		Map<String, String> jsonFileContents = new HashMap<>();
		
		jsonFileContents.put("leaves", 
				"{\n"+
				"  \"forge_marker\": 1,\n"+
				"  \"defaults\": {\n"+
				"	\"model\": \"##modid##:trees/##name##/##name##_leaves\"\n"+
				"  },\n"+
				"  \"variants\": {\n"+
				"	\"normal\": [{}],\n"+
				"	\"inventory\": [{}]\n"+
				"  }\n"+
				"}	\n"
		);

		jsonFileContents.put("log_item", 
				"{\n"+
				"  \"forge_marker\": 1,\n"+
				"\n"+
				"  \"variants\": {\n"+
				"  	\"type\" : {\n"+
				"	    \"large\" : { \"model\": \"##modid##:trees/##name##/##name##_log_large\" },\n"+
				"	    \"medium\" : { \n"+
				"	    	\"model\": \"##modid##:trees/##name##/##name##_log_medium\", \n"+
				"	    	\"transform\": {\n"+
				"		      \"gui\": { \"rotation\" : [ { \"x\" : 25 }, { \"y\" : 45 }, { \"z\" : 0 } ], \"scale\" : 0.5 },\n"+
				"		      \"ground\": { \"scale\" : 0.5 }\n"+
				"		    }\n"+
				"		},\n"+
				"	    \"small\" : { \n"+
				"	    	\"model\": \"##modid##:trees/##name##/##name##_log_small\", \n"+
				"	    	\"transform\": {\n"+
				"		      \"gui\": { \"rotation\" : [ { \"x\" : 25 }, { \"y\" : 45 }, { \"z\" : 0 } ], \"scale\" : 0.5 },\n"+
				"		      \"ground\": { \"scale\" : 0.5 }\n"+
				"		    }\n"+
				"	    },\n"+
				"	    \"damaged\" : { \"model\": \"##modid##:trees/##name##/##name##_log_small\" }\n"+
				"  	}\n"+
				"  }\n"+
				"}\n"
		);
		
		jsonFileContents.put("log", 
				"{\n"+
				"	\"forge_marker\": 0,\n"+
				"    \"multipart\": [\n"+
				"		{  \n"+
				"        	\"when\" : { \"type\" : \"large\", \"axis\" : \"y\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_large\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"large\", \"axis\" : \"x\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_large\", \"x\": 90, \"y\": 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"large\", \"axis\" : \"z\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_large\", \"x\": 90 }\n"+
				"        }\n"+
				"		\n"+
				"		,\n"+
				"	\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"medium\", \"axis\" : \"y\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"medium\", \"axis\" : \"x\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium\", \"x\": 90, \"y\": 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"medium\", \"axis\" : \"z\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium\", \"x\": 90 }\n"+
				"        },\n"+
				"        \n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"medium\", \"axis\" : \"x\", \"down\" : \"true\" }, { \"type\" : \"medium\", \"axis\" : \"z\", \"down\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium_connect\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"medium\", \"axis\" : \"x\", \"up\" : \"true\" }, { \"type\" : \"medium\", \"axis\" : \"z\", \"up\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium_connect\", \"x\" : 180 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"medium\", \"axis\" : \"x\", \"north\" : \"true\" }, { \"type\" : \"medium\", \"axis\" : \"y\", \"north\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium_connect\", \"x\" : 270 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"medium\", \"axis\" : \"x\", \"south\" : \"true\" }, { \"type\" : \"medium\", \"axis\" : \"y\", \"south\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium_connect\", \"x\" : 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"medium\", \"axis\" : \"z\", \"east\" : \"true\" }, { \"type\" : \"medium\", \"axis\" : \"y\", \"east\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium_connect\", \"x\" : 270, \"y\" : 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"medium\", \"axis\" : \"z\", \"west\" : \"true\" }, { \"type\" : \"medium\", \"axis\" : \"y\", \"west\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_medium_connect\", \"x\" : 90, \"y\" : 90 }\n"+
				"        }\n"+
				"        \n"+
				"    ,\n"+
				"        \n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"small\", \"axis\" : \"y\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"small\", \"axis\" : \"x\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small\", \"x\": 90, \"y\": 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"small\", \"axis\" : \"z\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small\", \"x\": 90 }\n"+
				"        },\n"+
				"        \n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"small\", \"axis\" : \"x\", \"down\" : \"true\" }, { \"type\" : \"small\", \"axis\" : \"z\", \"down\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small_connect\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"small\", \"axis\" : \"x\", \"up\" : \"true\" }, { \"type\" : \"small\", \"axis\" : \"z\", \"up\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small_connect\", \"x\" : 180 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"small\", \"axis\" : \"x\", \"north\" : \"true\" }, { \"type\" : \"small\", \"axis\" : \"y\", \"north\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small_connect\", \"x\" : 270 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"small\", \"axis\" : \"x\", \"south\" : \"true\" }, { \"type\" : \"small\", \"axis\" : \"y\", \"south\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small_connect\", \"x\" : 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"small\", \"axis\" : \"z\", \"east\" : \"true\" }, { \"type\" : \"small\", \"axis\" : \"y\", \"east\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small_connect\", \"x\" : 270, \"y\" : 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"small\", \"axis\" : \"z\", \"west\" : \"true\" }, { \"type\" : \"small\", \"axis\" : \"y\", \"west\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_small_connect\", \"x\" : 90, \"y\" : 90 }\n"+
				"        }\n"+
				"		\n"+
				"    ,\n"+
				"        \n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"damaged\", \"axis\" : \"y\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"damaged\", \"axis\" : \"x\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged\", \"x\": 90, \"y\": 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"type\" : \"damaged\", \"axis\" : \"z\" }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged\", \"x\": 90 }\n"+
				"        },\n"+
				"        \n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"damaged\", \"axis\" : \"x\", \"down\" : \"true\" }, { \"type\" : \"damaged\", \"axis\" : \"z\", \"down\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged_connect\" }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"damaged\", \"axis\" : \"x\", \"up\" : \"true\" }, { \"type\" : \"damaged\", \"axis\" : \"z\", \"up\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged_connect\", \"x\" : 180 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"damaged\", \"axis\" : \"x\", \"north\" : \"true\" }, { \"type\" : \"damaged\", \"axis\" : \"y\", \"north\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged_connect\", \"x\" : 270 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"damaged\", \"axis\" : \"x\", \"south\" : \"true\" }, { \"type\" : \"damaged\", \"axis\" : \"y\", \"south\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged_connect\", \"x\" : 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"damaged\", \"axis\" : \"z\", \"east\" : \"true\" }, { \"type\" : \"damaged\", \"axis\" : \"y\", \"east\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged_connect\", \"x\" : 270, \"y\" : 90 }\n"+
				"        },\n"+
				"        {  \n"+
				"        	\"when\" : { \"OR\" : [ { \"type\" : \"damaged\", \"axis\" : \"z\", \"west\" : \"true\" }, { \"type\" : \"damaged\", \"axis\" : \"y\", \"west\" : \"true\" } ] }, \n"+
				"        	\"apply\": { \"model\": \"##modid##:trees/##name##/##name##_log_damaged_connect\", \"x\" : 90, \"y\" : 90 }\n"+
				"        }\n"+
				"    ]\n"+
				"}\n"+
				"\n"
		);
		jsonFileContents.put("lumber", 
				"{\n"+
				"  \"forge_marker\": 1,\n"+
				"  \"defaults\": {\n"+
				"    \"model\": \"##modid##:trees/##name##/##name##_lumber\"\n"+
				"  },\n"+
				"  \"variants\": {\n"+
				"    \"inventory\": [{}]\n"+
				"  }\n"+
				"}\n"
		);
		jsonFileContents.put("lumberpile", 
				"{\n"+
				"  \"forge_marker\": 1,\n"+
				"\n"+
				"  \"variants\": {\n"+
				"  	\"pilesize\" : {\n"+
				"	    \"0\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [ 0.0625, 0.0, 0.0] } },\n"+
				"	    \"1\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [ 0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [ 0.3125, 0.0, 0.0] } } }\n"+
				"	    },\n"+
				"	    \"2\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } } }\n"+
				"	    },\n"+
				"	    \"3\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } } }\n"+
				"	    },\n"+
				"	    \"4\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } } }\n"+
				"	    },\n"+
				"	    \"5\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } } }\n"+
				"	    },\n"+
				"	    \"6\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } } }\n"+
				"	    },\n"+
				"	    \"7\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } } }\n"+
				"	    },\n"+
				"	    \"8\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } } }\n"+
				"	    },\n"+
				"	    \"9\" : { \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } } }\n"+
				"	    },\n"+
				"	    \"10\" :{ \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } },\n"+
				"	            \"lumber11\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.2500, 0.0] } }  }\n"+
				"	    },\n"+
				"	    \"11\" :{ \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } },\n"+
				"	            \"lumber11\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.2500, 0.0] } },\n"+
				"	            \"lumber12\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.2500, 0.0] } }  }\n"+
				"	    },\n"+
				"	    \"12\" :{ \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } },\n"+
				"	            \"lumber11\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.2500, 0.0] } },\n"+
				"	            \"lumber12\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.2500, 0.0] } },\n"+
				"	            \"lumber13\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } }  }\n"+
				"	    },\n"+
				"	    \"13\" :{ \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } },\n"+
				"	            \"lumber11\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.2500, 0.0] } },\n"+
				"	            \"lumber12\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.2500, 0.0] } },\n"+
				"	            \"lumber13\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber14\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } }  }\n"+
				"	    },\n"+
				"	    \"14\" :{ \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } },\n"+
				"	            \"lumber11\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.2500, 0.0] } },\n"+
				"	            \"lumber12\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.2500, 0.0] } },\n"+
				"	            \"lumber13\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber14\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber15\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } }  }\n"+
				"	    },\n"+
				"	    \"15\" :{ \"submodel\": {\n"+
				"	            \"lumber1\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.0, 0.0] } },\n"+
				"	            \"lumber2\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.0, 0.0] } },\n"+
				"	            \"lumber3\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.0, 0.0] } },\n"+
				"	            \"lumber4\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.0, 0.0] } },\n"+
				"	            \"lumber5\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber6\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber7\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber8\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.1250, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber9\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.0625, 0.2500, 0.0] } },\n"+
				"	            \"lumber10\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.3125, 0.2500, 0.0] } },\n"+
				"	            \"lumber11\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.5625, 0.2500, 0.0] } },\n"+
				"	            \"lumber12\":{ \"model\": \"##modid##:trees/##name##/##name##_lumber\", \"transform\" : { \"translation\" : [0.8125, 0.2500, 0.0] } },\n"+
				"	            \"lumber13\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.0625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber14\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.3125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber15\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.5625], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } },\n"+
				"	            \"lumber16\": { \"model\": \"##modid##:trees/##name##/##name##_lumber\",\n"+
				"	            	\"transform\" : { \"translation\" : [0.0, 0.3750, -0.8125], \"rotation\" : [{\"y\":90}], \"scale\" : 1.0 } }  }\n"+
				"	    }\n"+
				"    }\n"+
				"  }\n"+
				"}\n"
		);
		jsonFileContents.put("root", 
				"{\n"+
				"  \"forge_marker\": 1,\n"+
				"  \"defaults\": {\n"+
				"    \"model\": \"##modid##:trees/##name##/##name##_root\"\n"+
				"  },\n"+
				"  \"variants\": {\n"+
				"    \"normal\": [{}],\n"+
				"    \"inventory\": [{}]\n"+
				"  }\n"+
				"}\n"
		);
		
		return jsonFileContents;
	}
	
	private static Map<String, String> getModelFilesContent(String treeName) {
		Map<String, String> jsonFileContents = new HashMap<>();
		
		jsonFileContents.put(treeName+"_leaves", 
				"{\n"+
				"    \"parent\": \"block/leaves\",\n"+
				"    \"textures\": {\n"+
				"        \"all\": \"##modid##:blocks/trees/##name##/##name##_leaves\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_large", 
				"{\n"+
				"    \"parent\": \"block/cube_column\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_top\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_side\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_damaged", 
				"{\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/log_small\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_top\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_damaged\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_damaged_connect", 
				"{\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/log_small_connect\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_top\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_damaged\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_medium", 
				"{\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/log_medium\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_top\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_side\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_medium_connect", 
				"{\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/log_medium_connect\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_side\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_side\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_small", 
				"{\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/log_small\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_top\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_side\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_log_small_connect", 
				"{\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/log_small_connect\",\n"+
				"    \"textures\": {\n"+
				"        \"end\": \"##modid##:blocks/trees/##name##/##name##_log_side\",\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_side\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_lumber", 
				"{\n"+
				"    \"forge_marker\": 1,\n"+
				"    \"parent\": \"##modid##:block/trees/_abstract/lumber\",\n"+
				"    \"textures\": {\n"+
				"        \"side\": \"##modid##:blocks/trees/##name##/##name##_log_damaged\"\n"+
				"    }\n"+
				"}\n"
		);
		jsonFileContents.put(treeName+"_root", 
				"{\n"+
				"    \"parent\": \"block/cube_all\",\n"+
				"    \"textures\": {\n"+
				"        \"all\": \"##modid##:blocks/trees/##name##/##name##_log_side\"\n"+
				"    }\n"+
				"}\n"
		);
		
		return jsonFileContents;
	}

	private static String getNewTreeTECode(String modid, String modblocksclassname, String treeName) {
		String code =
				"package ##modid##.trees.tileentity;\n"+
				"\n"+
				"import java.util.stream.IntStream;\n"+
				"\n"+
				"import net.minecraft.util.math.BlockPos;\n"+
				"import ##modid##.core.##blockz##;\n"+
				"\n"+
				"public final class ##name##TreeRootTE extends TreeRootTE {\n"+
				"\n"+
				"	public ##name##TreeRootTE() {\n"+
				"		this.leavesBlock = ##blockz##.##name##Tree.leaves; \n"+
				"		this.logBlock = ##blockz##.##name##Tree.log; \n"+
				"		this.growthRate = 20;\n"+
				"		this.trunkSectionMaxLength = new int[] { 4, 3, 2 };\n"+
				"		this.trunkMaxHeight = IntStream.of(trunkSectionMaxLength).sum();\n"+
				"		this.leavesMinHeight = 5;\n"+
				"		this.branchMinHeight = trunkMaxHeight - 4;\n"+
				"		this.branchHeightSpread = 2;\n"+
				"		this.branchGrowthSpeed = 40;\n"+
				"		this.canGrowMultiBranch = true;\n"+
				"	}\n"+
				"	\n"+
				"	public BlockPos getNextTrunkPos(BlockPos target) {\n"+
				"		return target.up();\n"+
				"	}\n"+
				"	\n"+
				"	public BlockPos getPrevTrunkPos(BlockPos target) {\n"+
				"		return target.down();\n"+
				"	}\n"+
				"\n"+
				"	public int getBranchMaxLength(BlockPos target) {\n"+
				"		return getHeight(target) / 2;\n"+
				"	}\n"+
				"}\n"+
				"\n";
				
		
		return code
				.replace("##modid##", modid)
				.replace("##blockz##", modblocksclassname) 
				.replace("##name##", treeName) ;
	}

	private static String getRegistrarSubclassCode(String treeName, String treeNameU) {
		String code = 
				"@GameRegistry.ObjectHolder(PrimitiMod.MODID)\n"+
				"	public static class ##uname##Tree extends TreeRegistrar {\n"+
				"		protected static final String treeName = \"##name##\";\n"+
				"		protected static final String prefix = treesDir+treeName+\"/\";\n"+
				"	    @GameRegistry.ObjectHolder(prefix+rootName)\n"+
				"	    public static BlockTreeRoot root;\n"+
				"	    @GameRegistry.ObjectHolder(prefix+logName)\n"+
				"	    public static BlockComplexLog log;\n"+
				"	    @GameRegistry.ObjectHolder(prefix+leavesName)\n"+
				"	    public static BlockFallingLeaves leaves;\n"+
				"	    @GameRegistry.ObjectHolder(prefix+lumberPileName)\n"+
				"	    public static BlockLumberPile lumberPile;\n"+
				"	    @GameRegistry.ObjectHolder(prefix+lumberName)\n"+
				"	    public static ItemLumber lumber;\n"+
				"	};\n"+
				"	"+registrarTag+"\n";
		return code
				.replace("##name##", treeName)
				.replace("##uname##", treeNameU) ;
	}
	
	private static String getBlockRegisterCode(String treeNameWPref, String treeNameU) {
		String code = 
				"##uname##Tree.registerBlocks(##uname##Tree.treeName, ##unamete##TreeRootTE.class, event.getRegistry());\n"+
				"    	"+registerBlockTag+"\n";
		
		return code.replace("##uname##", treeNameWPref)
				   .replace("##unamete##", treeNameU);
	}
	
	private static String getItemRegisterCode(String treeNameWPref) {
		String code = 
				"##uname##Tree.registerItems(##uname##Tree.treeName, event.getRegistry(), ##uname##Tree.lumberPile);\n"+
				"    	"+registerItemTag+"\n";
		
		return code.replace("##uname##", treeNameWPref);
	}
	
	private static String getItemBlockRegisterCode(String treeNameWPref) {
		String code = 
				"##uname##Tree.registerItemBlocks(event.getRegistry(), ##uname##Tree.log, ##uname##Tree.leaves, ##uname##Tree.root);\n"+
				"    	"+registerItemBlockTag+"\n";
		
		return code.replace("##uname##", treeNameWPref);
	}
	
	private static String getInitModelsCode(String treeNameWPref) {
		String code = 
				"##uname##Tree.initItemModels(##uname##Tree.log, ##uname##Tree.leaves, ##uname##Tree.root, ##uname##Tree.lumberPile, ##uname##Tree.lumber);\n"+
				"    	"+initModelsTag+"\n";
		
		return code.replace("##uname##", treeNameWPref);
	}
	
	private static String getColourRegisterCode(String treeNameWPref) {
		String code = 
				"##uname##Tree.registerColourHandlers(##uname##Tree.leaves);\n"+
				"    	"+registerColourTag+"\n";
		
		return code.replace("##uname##", treeNameWPref);
	}
	
	private static String getImportTECode(String treeNameWPref, String modid) {
		String code = 
				"import ##modid##.trees.tileentity.##uname##TreeRootTE;\n"+
				importTETag+"\n";
		
		return code.replace("##uname##", treeNameWPref)
				   .replace("##modid##", modid);
	}

}
