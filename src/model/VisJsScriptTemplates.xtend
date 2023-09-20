package model

class VisJsScriptTemplates {

	/**
	 * Returns the javascript-template used for embedding vis.js into the JavaFX webview 
	 * 
	 * @author maximiliansell
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
						          	 				navigationButtons: true,
						          	 			keyboard: true
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

	/**
	 * Adds an "eclass"-node to the current javascript displayed in the webview
	 * 
	 * @param id - the id of the to be created node
	 * @param label - label/name of the to be created node
	 * @param attributes - attributes of the to be created node
	 */
	def static String addNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: "html", bold: '16px arial black', ital : '10px cursive',align : "left"}, label:"<b>«label»</b>"+"\n"+"«attributes»", shape: "box", color: { background: "#93bae2",border: "black"}});
		          attrNodes.set("«id»","<b>"+"«label»"+"</b>\n"+"«attributes»");
				  labelNodes.set("«id»", "<b>"+"«label»"+"</b>");
				  optionNodes.set("«id»", "#93bae2");'''
	}

	/**
	 * Adds an "eenum"-node to the current javascript displayed in the webview
	 * 
	 * @param id - the id of the to be created node
	 * @param label - label/name of the to be created node
	 * @param attributes - attributes of the to be created node
	 */
	def static String addEnumNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: "html", bold: '16px arial black',ital : '10px cursive',align : "left"}, label: "<i><<EEnum>></i> \n <b>«label»</b>\n"+"«attributes»", shape: "box", color: {  background: "#e2bb93",border: "black"}});
		          attrNodes.set("«id»","<i><<EEnum>></i>\n"+"<b>"+"«label»"+"</b>\n"+"«attributes»");
				  labelNodes.set("«id»", "<b>"+"«label»"+"</b>\n");
				  optionNodes.set("«id»", "#e2bb93");'''
	}

	/**
	 * Adds an "abstract"-node to the current javascript displayed in the webview
	 * 
	 * @param id - the id of the to be created node
	 * @param label - label/name of the to be created node
	 * @param attributes - attributes of the to be created node
	 */
	def static String addAbstractNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: "html", bold: '16px arial black',ital : '10px cursive',align : "left"}, label: "<i><<Abstract>></i> \n <b>«label»</b>\n"+"«attributes»", shape: "box", color: { background: "#e3edf7",border: "black"}});
		          attrNodes.set("«id»","<i><<Abstract>></i>\n"+"<b>"+"«label»"+"</b>\n"+"«attributes»");
				  labelNodes.set("«id»", "<b>"+"«label»"+"</b>\n");
				  optionNodes.set("«id»", "#e3edf7");'''
	}

	/**
	 * Adds an "interface"-node to the current javascript displayed in the webview
	 * 
	 * @param id - the id of the to be created node
	 * @param label - label/name of the to be created node
	 * @param attributes - attributes of the to be created node
	 */
	def static String addInterfaceNode(Integer id, String label, String attributes) {
		return '''nodes.add({ id: "«id»",font: { multi: "html", bold: '16px arial black',ital : '10px cursive',align : "left" }, label: "<i><<Interface>></i> \n <b>«label»</b>", shape: "box", color: { background: "#c1e1c1",border: "black"}});
		          attrNodes.set("«id»","<i><<Interface>></i>\n"+"<b>"+"«label»"+"</b>\n"+"«attributes»");
				  labelNodes.set("«id»", "<b>"+"«label»"+"</b>\n");
				  optionNodes.set("«id»","#c1e1c1");'''
	}

	/**
	 * Adds an edge (reference of another node) to the current javascript displayed in the webviews
	 * 
	 * @param from - id of the reference-owner node
	 * @param to - if of the node that the reference points to
	 * @param label - name of the reference and the associated multiplicity
	 */
	def static String addEdge(String from, String to, String label) {
		return '''edges.add({from: «from», to: «to», label:"«label»", font: {align: "bottom" }, length : 200, arrows: {from:{ enabled: true, type:"diamond",}, to: {enabled: true, type:"vee",},},});'''
	}

	/**
	 * Adds a (heridity-)edge to the current javascript displayed in the webview
	 * 
	 * @param from - id of node that inherits from a different node
	 * @param to - id of the node that inherits to another node
	 */
	def static String addHeridityEdge(String from, String to) {
		return '''edges.add({from: «from», to: «to», font: {align: "bottom" }, length : 200, arrows: {from:{enabled: true, type:"triangle",},},});'''
	}

	/**
	 * Adds an (implements-)edge to the current javascript displayed in the webview
	 * 
	 * @param from - id of the node that implements a certain (interface-)node
	 * @param to - id of the to be implemented node
	 */
	def static String addImplementsEdge(String from, String to) {
		return '''edges.add({from: «from», to: «to», font: {align: "bottom" }, length : 200, label: "implements", dashes: true, arrows: {from:{enabled: true, type:"triangle",},},});'''
	}

	/**
	 * Adds a bidirectional edge to the current javascript displayed in the webview
	 * 
	 * @param from - id of the node containing the bidirectional reference
	 * @param to - if of node containing the bidirectional reference
	 * @param label - name of the references and the associated multiplicities
	 */
	def static String addBiDirEdge(String from, String to, String label) {
		return '''edges.add({from: «from», to: «to», label:"«label»", font: {align: "bottom" }, length : 200, arrows: {from:{enabled: true, type:"vee",}, to: {enabled: true, type:"vee",},},});'''
	}

	/**
	 * Clears the the edges-/nodes-DataSet of the current javascript displayed in the webview and therefore clearing the whole network
	 */
	def static String destroyNetwork() {
		return '''
			edges.clear();
			nodes.clear();
		'''
	}

	/**
	 * Adds a "click-on-node"-listener to the network provided by vis.js that enables to update the nodes of the current javascript displayed in the webview 
	 * -> label will be shortened to just the name of the node or label will be expanded by the attributes of the node  
	 */
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

	/**
	 * 
	 * Adds a "click-on-node"-listener to the network provided by vis.js that enables to update the nodes of the current javascript displayed in the webview 
	 * -> color and border of the nodes will be updated to the provided params
	 * 
	 * @param color - new color of the node
	 * @param border - new border-color of the node
	 */
	def static String clickOnNetworkHighlight(String color, String border) {
		return '''
		network.on("selectNode", function (params) {
			params.nodes.forEach((nodeId) =>
				network.body.nodes[nodeId].setOptions({
					color: {background: "«color»",border: "«border»"}
					})
					);
						});'''
	}

	/**
	 * Removes the "click-on-node"-listener to the network
	 */
	def static String removeClickOnNetworkHighlight() {
		return '''
			network.off("selectNode");  	
		'''
	}

	/**
	 * Label of the provided range of nodes will be shortened to just the name of the node
	 * 
	 * @param idCounter - id to of the nodes to be counted down from 
	 */
	def static String hideAllAttributes(Integer idCounter) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
			nodes.update({id:i.toString(),font: { multi: "html", bold: '16px arial black',ital : '10px cursive',align : "left"}, label : labelNodes.get(i.toString())});
		}'''
	}

	/**
	 * Label of the provided range of nodes will be expanded by the attributes of the node
	 * 
	 * @param idCounter - id of the nodes to be counted down from 
	 */
	def static String showAllAttributes(Integer idCounter) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
			nodes.update({id:i.toString(),font: { multi: "html", bold: '16px arial black',ital : '10px cursive',align : "left"}, label : attrNodes.get(i.toString())});
		}'''
	}

	/**
	 * Changes the color of the specified node to a set color
	 * 
	 * @param id - id of the nodes to be counted down from 
	 */
	def static String hightlightChoiceNodes(Integer id) {
		return '''nodes.update({id:"«id»", color : {background: "#ffb347",border: "#ae6500"}});'''
	}

	/**
	 * Changes the color of the specified node to the given color
	 * 
	 * @param id - id of the to be changed node
	 * @param color - new color of the node
	 * @param border - new border-color of the node
	 */
	def static String hightlightColorNodes(Integer id, String color, String border) {
		return '''nodes.update({id:"«id»", color : {background: "«color»",border: "«border»"}});'''
	}

	/**
	 * Changes the color of all nodes to the given color
	 * 
	 * @param id - id of the nodes to be counted down from
	 * @param color - new color of the node
	 * @param border - new border-color of the node
	 */
	def static String hightlightAllColorNodes(Integer idCounter, String color, String border) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
					nodes.update({id:i.toString(), color : {background: "«color»",border: "«border»"}});
				}'''
	}

	/**
	 * 
	 * Color of the provided range of nodes will be changed to their default
	 * 
	 * @param idCounter - id of the nodes to be counted down from 
	 */
	def static String deHightlightChoiceNodes(Integer idCounter) {
		return '''for (let i=«idCounter»; i >= 0; i--) {
					nodes.update({id:i.toString(), color : {background: optionNodes.get(i.toString()),border: "black"}});
				}'''
	}

	/**
	 * Hides the node with the given id
	 * 
	 * @param id - id of the to be hidden node
	 */
	def static String hideNode(Integer id) {
		return '''nodes.update({id:"«id»", hidden : true});'''
	}

	/**
	 * Shows the node with the given id
	 * 
	 * @param id - id of the hidden node
	 */
	def static String showNode(Integer id) {
		return '''nodes.update({id:"«id»", label : attrNodes.get("«id»") , shape: "box", color: { background: optionNodes.get("«id»"),border: "black"}, hidden : false});'''
	}

	/**
	 * Removes the "click-on-node"-listener to the network
	 */
	def static String removeClickOnNetworkShowAttributes() {
		return '''
			network.off("selectNode");  
			network.off("deselectNode");	
		'''
	}
}
