package model

import java.util.Map
import java.util.Collection
import java.util.Map.Entry
import java.util.LinkedList
import java.util.ArrayList

class VisJsScriptTemplates {
	/**
	 * 
	 */
	def static String getJSTemplate() {
		return '''
			<!DOCTYPE html>
						<html lang="en">
						<head>
						<title>Network</title>
						<script
						type="text/javascript"
						src="https://unpkg.com/vis-network/standalone/umd/vis-network.min.js"
						></script>
						<style type="text/css">
						#mynetwork {
						width: 500px;
						height: 350px;
						border: 1px solid lightgray;
						}
						</style>
						</head>
						<body>
						<div id="mynetwork"></div>
						<script type="text/javascript">
						var nodes = new vis.DataSet([
						]);
						// create an array with edges
						var edges = new vis.DataSet([	
						]);
						// create a network
						var container = document.getElementById("mynetwork");
						var data = {
						       nodes: nodes,
						       edges: edges,
						     };
						          var options = {
						          	  autoResize: true,
						          	 physics: {
						          	 	    barnesHut: {
						          	 	      gravitationalConstant: -50000 
						          	 	    },
						          	     "enabled": true,
						          	     avoidOverlap: 1,
						          	     stabilization: {
						          	     	enabled : true,
						          	     	iterations : 200
						          	     	},
						          	 },
						          	 			interaction: { 
						          	 				multiselect: true,
«««						          	 				dragNodes: true,
						          	 				navigationButtons: true,
						          	 			keyboard: true
						          	 			},
«««						          	 			    			                   	 				  edges: {
«««						          	 			    			                   	 				    smooth: {
«««						          	 			    			                   	 				      type: "continuous",
«««						          	 			    			                   	 				    },
«««						          	 			    			                   	 				  },
						          	 			   	 			    			 repulsion: {
						          	 			   	 			    			  springLength: 1000,
						          	 			   	 			    			  nodeDistance: 1000,
						          	 				 			    			 },
						          	 	 };
					var network = new vis.Network(container, data, options); 	      	
					var labelNodes = new Map();
					var attrNodes = new Map();
					var optionNodes = new Map();		
					  	   </script>
					  	 </body>
					  </html>
		'''
	}

//parametriesieren ud kürzen
	def static String addNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: { background: "#93bae2",border: "black"}});
		          attrNodes.set("«id»","<code>"+"«label»"+"</code>\n"+"«attributes»");
				  labelNodes.set("«id»", "«label»");
				  optionNodes.set("«id»", "#93bae2");'''
	}

	def static String addEnumNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<i>EEnum</i> \n<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: {  background: "#e2bb93",border: "black"}});
		          attrNodes.set("«id»","<code>"+"«label»"+"</code>\n"+"«attributes»");
				  labelNodes.set("«id»", "«label»");
				  optionNodes.set("«id»", "#e2bb93");'''
	}

	def static String addAbstractNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<i>Abstract</i> \n<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: { background: "#e3edf7",border: "black"}});
		          attrNodes.set("«id»","<code>"+"«label»"+"</code>\n"+"«attributes»");
				  labelNodes.set("«id»", "«label»");
				  optionNodes.set("«id»", "#e3edf7");'''
	}

	def static String addInterfaceNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<i>Interface</i> \n<code>"+"«label»", shape: "box", color: { background: "#c1e1c1",border: "black"}});
		          attrNodes.set("«id»","<code>"+"«label»"+"</code>\n"+"«attributes»");
				  labelNodes.set("«id»", "«label»");
				  optionNodes.set("«id»","#c1e1c1");'''
	}

	def static String addEdge(String from, String to, String label) {
		return '''edges.add({from: «from», to: «to», label:"«label»", font: {align: "bottom" }, arrows: {from:{ enabled: true, type:"diamond",}, to: {enabled: true, type:"vee",},},});'''
	}

	def static String addHeridityEdge(String from, String to) {
		return '''edges.add({from: «from», to: «to», font: {align: "bottom" }, arrows: {from:{enabled: true, type:"triangle",},},});'''
	}

	def static String addImplementsEdge(String from, String to) {
		return '''edges.add({from: «from», to: «to», font: {align: "bottom" }, label: "implements", dashes: true, arrows: {from:{enabled: true, type:"triangle",},},});'''
	}

	def static String addBiDirEdge(String from, String to, String label) {
		return '''edges.add({from: «from», to: «to», label:"«label»", font: {align: "bottom" }, arrows: {from:{enabled: true, type:"vee",}, to: {enabled: true, type:"vee",},},});'''
	}

	def static String configureNode(String id, Collection<String> properties) {
		return '''nodes.update({id: «id», «FOR prop : properties SEPARATOR ", "»«prop»«ENDFOR»});'''
	}

	def static String removeNode(String id) {
		return '''nodes.remove(«id»)'''
	}

	def static String removeEdge(String id) {
		return '''edges.remove(«id»)'''
	}

	def static String destroyNetwork() {
		return '''
			edges.clear();
			nodes.clear();
		'''
	}

	def static String clickOnNetworkShowAttributes() {
		return '''
			network.on("selectNode", function (params) {
				params.nodes.forEach((nodeId) =>
					network.body.nodes[nodeId].setOptions({
						label : attrNodes.get(nodeId)
						})
						);
							});
					
			network.on("deselectNode", function (params) {
					params.previousSelection.nodes.forEach((nodeId) =>
								network.body.nodes[nodeId.id].setOptions({
									label : labelNodes.get(nodeId.id)
									})
									);
										});
		'''
	}

	def static String hideAllAttributes(Integer idCounter) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
			nodes.update({id:i.toString(), label : labelNodes.get(i.toString())});
		}'''
	}

	def static String showAllAttributes(Integer idCounter) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
			nodes.update({id:i.toString(), label : attrNodes.get(i.toString())});
		}'''
	}

	def static String hightlightChoiceNodes(Integer id) {
		return '''nodes.update({id:"«id»", color : {background: "#ffb347",border: "#ae6500"}});'''
	}

	def static String deHightlightChoiceNodes(Integer idCounter) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
					nodes.update({id:i.toString(), color : {background: optionNodes.get(i.toString()),border: "black"}});
				}'''
	}

	def static String hideNode(Integer id) {
		return '''nodes.update({id:"«id»", hidden : true});'''
	}

	def static String showNode(Integer id) {
		return '''nodes.update({id:"«id»", label : attrNodes.get("«id»") , shape: "box", color: { background: optionNodes.get("«id»"),border: "black"}, hidden : false});'''
	}

	def static String removeClickOnNetworkShowAttributes() {
		return '''
			network.off("selectNode");  
			network.off("deselectNode");	
		'''
	}
}
