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
			           	 layout: {			                  	   
			           	  },
			           	 edges: {
			           	 			        style: 'arrow'
			           	 			    },
			           	 			manipulation: {
			           	 				enabled: true,
			           	 				addNode: true,
			           	 				},
			           	 			interaction: { 
			           	 				dragNodes: true 
			           	 			},
«««			           	 			    physics: {
«««			           	 			    	barnesHut: {
«««			           	 			    			theta : 0.2,
«««			           	 			    			    avoidOverlap: 0.35,
«««			           	 			    			  },
			           	 			    			                   	 				  edges: {
			           	 			    			                   	 				    smooth: {
			           	 			    			                   	 				      type: "continuous",
			           	 			    			                   	 				    },
			           	 			    			                   	 				  },
						                	 			    			 repulsion: {
			                	 			    			     springLength: 1000,
			                	 			    			     nodeDistance: 1000,
			                	 			    			 },
«««			                	 			    			 stabilization: true
«««			                	 			    },
			           			 };
			var network = new vis.Network(container, data, options); 	               
			  	   </script>
			  	 </body>
			  </html>
		'''
	}

//parametriesieren ud kürzen
	def static String addXMINode(Integer id, String label) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "«label»", shape: "box", color: { background: "#f4f98b",border: "black"}});'''
	}

	def static String addXMIEnumNode(Integer id, String label) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "«label»", shape: "box", color: {  background: "#7575f9",border: "black"}});'''
	}

	def static String addXMIAbstractNode(Integer id, String label) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "«label»", shape: "box", color: { background: "#9ecaf7",border: "black"}});'''
	}

	def static String addXMIInterfaceNode(Integer id, String label) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "«label»", shape: "box", color: { background: "#ff40ff",border: "black"}});'''
	}

	def static String addNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: { background: "#f9de8b",border: "black"}});'''
	}

	def static String addEnumNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<i>EEnum</i> \n<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: {  background: "#7575f9",border: "black"}});'''
	}

	def static String addAbstractNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<i>Abstract</i> \n<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: { background: "#9ecaf7",border: "black"}});'''
	}

	def static String addInterfaceNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: true }, label: "<i>Interface</i> \n<code>"+"«label»"+"</code>\n"+"«attributes»", shape: "box", color: { background: "#ff40ff",border: "black"}});'''
	}

	def static String addNodes(Map<String, String> nodes) {
		return '''«FOR node : nodes.keySet SEPARATOR "\n"»nodes.add({ id: "«node»",font: { multi: true }, label: "«nodes.get(node)»", shape: "box", color: { background: "#f9de8b",border: "black"}});«ENDFOR»'''
	}

	def static String addEdge(String from, String to, String label) {
		return '''edges.add({from: «from», to: «to», label:"«label»", font: {align: "bottom" }, length: 250, arrows: {from:{ enabled: true, type:"diamond",}, to: {enabled: true, type:"vee",},},});'''
	}

	def static String addHeridityEdge(String from, String to) {
		return '''edges.add({from: «from», to: «to», font: {align: "bottom" }, length: 500, arrows: {from:{enabled: true, type:"triangle",},},});'''
	}

	def static String addImplementsEdge(String from, String to) {
		return '''edges.add({from: «from», to: «to», font: {align: "bottom" }, label: "implements", dashes: true, length: 500, arrows: {from:{enabled: true, type:"triangle",},},});'''
	}

	def static String addBiDirEdge(String from, String to, String label) {
		return '''edges.add({from: «from», to: «to», label:"«label»", font: {align: "bottom" }, length: 250, arrows: {from:{enabled: true, type:"vee",}, to: {enabled: true, type:"vee",},},});'''
	}

	// here
	def static String addXMINodes(Map<Integer, Entry<String, String>> nodes) {
		return '''«FOR node : nodes.keySet SEPARATOR "\n"»nodes.add({ id: "«node»",font: { multi: true }, label: "«nodes.get(node).value»", shape: "box", color: { background: "#f9de8b",border: "black"}});«ENDFOR»'''
	}

	def static String addEnumNodes(Map<String, String> nodes) {
		return '''«FOR node : nodes.keySet SEPARATOR "\n"»nodes.add({ id: "«node»",font: { multi: true }, label: "«nodes.get(node)»", shape: "box", color: { background: "#7575f9",border: "black"}});«ENDFOR»'''
	}

	def static String addAbstractNodes(Map<String, String> nodes) {
		return '''«FOR node : nodes.keySet SEPARATOR "\n"»nodes.add({ id: "«node»",font: { multi: true }, label: "«nodes.get(node)»", shape: "box", color: { background: "#d3d3d3",border: "black"}});«ENDFOR»'''
	}

	def static String addInterfaceNodes(Map<String, String> nodes) {
		return '''«FOR node : nodes.keySet SEPARATOR "\n"»nodes.add({ id: "«node»",font: { multi: true }, label: "«nodes.get(node)»", shape: "box", color: { background: "#9ecaf7",border: "black"}});«ENDFOR»'''
	}

	def static String addEdges(Map<String, Entry<String, String>> edges) {
		return '''«FOR edge : edges.keySet SEPARATOR "\n"»edges.add({from: «edges.get(edge).key», to: «edges.get(edge).value», label:"«edge»", font: {align: "bottom" }, length: 250, arrows: {from:{ enabled: true, type:"diamond",}, to: {enabled: true, type:"vee",},},});«ENDFOR»'''
	}

	def static String addBidirectionalEdges(Map<String, Entry<String, String>> edges) {
		return '''«FOR edge : edges.keySet SEPARATOR "\n"»edges.add({from: «edges.get(edge).key», to: «edges.get(edge).value», label:"«edge»", font: {align: "bottom" }, length: 250, arrows: {from:{enabled: true, type:"vee",}, to: {enabled: true, type:"vee",},},});«ENDFOR»'''
	}

	def static String addHeridityEdges(Map<String, Entry<String, String>> edges) {
		return '''«FOR edge : edges.keySet SEPARATOR "\n"»edges.add({from: «edges.get(edge).key», to: «edges.get(edge).value», font: {align: "bottom" }, length: 500, arrows: {from:{enabled: true, type:"triangle",},},});«ENDFOR»'''
	}

	def static String addImplementsEdges(Map<String, Entry<String, String>> edges) {
		return '''«FOR edge : edges.keySet SEPARATOR "\n"»edges.add({from: «edges.get(edge).key», to: «edges.get(edge).value», font: {align: "bottom" }, label: "implements", dashes: true, length: 500, arrows: {from:{enabled: true, type:"triangle",},},});«ENDFOR»'''
	}

	def static String configureNode(String id, Collection<String> properties) {
		return ''' nodes.update({id: «id», «FOR prop : properties SEPARATOR ", "»«prop»«ENDFOR»});'''
	}

	def static String removeNode(String id) {
		return ''' nodes.remove(«id»)'''
	}

	def static String removeEdge(String id) {
		return ''' edges.remove(«id»)'''
	}

	def static String destroyNetwork() {
		return '''
			edges.clear();
			nodes.clear();
		'''
	}

}
