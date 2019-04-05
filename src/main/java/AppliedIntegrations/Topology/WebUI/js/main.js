/**
 * @Author Azazell
 */

// Create vis node set
var nodes = new vis.DataSet();

// create an array with edges
var edges = new vis.DataSet();

// Create name translation map
var stringTranslationMap = new Map();

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
                visNode.label = node.split("@", 1)[0];

                // Add vis node
                nodes.add(visNode)
            });

            // Iterate for source length, as edge destination length is always equal
            for (var i = 0; i < network.src.length; i++){
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
var container = document.getElementById('mynetwork');
var data = {
  nodes: nodes,
  edges: edges
};
var options = {};
var network = new vis.Network(container, data, options);