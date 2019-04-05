/**
 * @Author Azazell
 */

// Create vis node set
var nodes = new vis.DataSet();

// create an array with edges
var edges = new vis.DataSet();

// Currently selected node
var selectedNode;

// Create name translation map
var stringTranslationMap = new Map();

// Create message array
var nodeMessages = [];

// jQuery function
(function($) {
    // Wait for ready state
    $(document).ready(function() {
        // Get network // Create network manager
        $.getJSON("Network.json", function(network) {
            // Iterate for_each node
            $.each(network.nodes, function(i, node) {
                // Create vis node
                var visNode = new Object();

                // Change it's id to i
                visNode.id = i;

                // Map object
                stringTranslationMap.set(node, i);

                // Change it's label to name of node
                visNode.label = node.split('@', 1)[0];

                // Add vis node
                nodes.add(visNode)

                // Add data of node to map
                nodeMessages.push(network.data[i]);
            });

            // Iterate for source length, as edge destination length is always equal
            for (var i = 0; i < network.src.length; i++){
                // Should this iteration be skipped?
                var skip = false;

                // Check if src not equal to dest
                if(stringTranslationMap.get(network.src[i]) == stringTranslationMap.get(network.dest[i]))
                    // Skip
                    continue;

                // Do not add more than one same edge
                // Iterate for each element of edges
                edges.forEach(function(edge, index){
                    // Check if variables of edge is not equal to newly generated
                    if(edge.from == stringTranslationMap.get(network.src[i]) && edge.to == stringTranslationMap.get(network.dest[i]))
                        // Skip
                        skip = true;
                }, undefined);

                // Check if skip was queried
                if(skip)
                    // Skip
                    continue;

                // Create edge object
                var edge = new Object;

                // Fill object from translation map
                edge.from = stringTranslationMap.get(network.src[i]); // (1)
                edge.to = stringTranslationMap.get(network.dest[i]); // (2)

                // Add edge to edge list
                edges.add(edge);
            }


        });
    })
})(this.jQuery)

// create a network
var container = document.getElementById('network');

var data = {
  nodes: nodes,
  edges: edges
};

var options = {};

// Create network
var network = new vis.Network(container, data, options);

// Create click event
network.on( 'click', function(properties) {
    // Get node ids
    var ids = properties.nodes;

    // Get clicked nodes
    var clickedNodes = nodes.get(ids);

    // Check if size greater than 0
    if(clickedNodes.length > 0){
        // Write 1st node
        selectedNode = clickedNodes[0];

        // Get node message
        var message = nodeMessages[ids[0]];

        // Create html tag
        var innerHTML = "<p> Summary for node: " + selectedNode.label + " </p>";

        // List of keys
        var keys = [];

        // List of values
        var values = new Map;

        // Iterate for each element of array
        Object.entries(message).forEach(entry => {
          // Get key
          keys.push(entry[0]);

          // Get val
          values.set(entry[0], entry[1]);
        });

        // Sort keys
        keys.sort();

        // Iterate for keys length
        for(var i = 0; i < keys.length; i++){
            innerHTML += "<h3> " + keys[i] + " : " + values.get(keys[i]) + " </h3> "
        }

        // Change inner html of element
        document.getElementById("node_panel").innerHTML = innerHTML;
    }
});