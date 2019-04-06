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

// Map of all categories
var categoryMap = new Map();

// Category of network data
var networkDataList = ["Active", "Usage"]

// Category of grid flags
var gridFlagDataList = ["CANNOT_CARRY", "CANNOT_CARRY_COMPRESSED", "COMPRESSED_CHANNEL", "DENSE_CAPACITY", "MULTIBLOCK",
                        "PREFERRED", "REQUIRE_CHANNEL"]

// Category of position
var positionDataList = ["X", "Y", "Z"]

// Set key array
var keysArray = ["Network Data", "Grid Flags", "Position"]

// Set category map entries
categoryMap.set("Network Data", networkDataList); // (1)
categoryMap.set("Grid Flags", gridFlagDataList); // (2)
categoryMap.set("Position", positionDataList); // (3)

// jQuery function
(function($) {
    // Wait for ready state
    $(document).ready(function() {
        // Get network // Create network manager
        $.getJSON("json", function(network) {
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

                console.log(network.data[i].Hex.toString(16));

                // Change color of node
                visNode.color = "#" + network.data[i].Hex.toString(16);

                // Check if color is code of transparent color
                if(network.data[i].Hex == 9002152)
                    // Change color to color of fluix crystal
                    visNode.color = "#343161";

                // Change shape to dot
                visNode.shape = "dot";

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

// Last node clicked
var lastNode;

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
        var innerHTML = "<p> Summary for node: " + selectedNode.label + " </p>"

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

        // Check if selected node has changed
        if(selectedNode != lastNode){
            // Update last node
            lastNode = selectedNode;

            // Clear all text from tabs
            // Iterate for each key
            keys.forEach(function(innerKey, i){
                // Iterate for each category key
                keysArray.forEach(function(category, i){
                    // Clear text
                    document.getElementById(category).innerHTML = "";
                })
            })
        }

        // Iterate for each category key
        keysArray.forEach(function(category, i){
            // Add text to element
            document.getElementById(category).innerHTML += innerHTML;
        })

        // Sort each map
        // Iterate for each key
        keys.forEach(function(innerKey, i){
            // Iterate for each category key
            keysArray.forEach(function(category, i){
                categoryMap.get(category).sort();
            });
        });

        // Iterate for each key
        keys.forEach(function(innerKey, i){
            // Iterate for each category key
            keysArray.forEach(function(category, i){
                // Iterate for each list of category
                categoryMap.get(category).forEach(function(key, i){
                    // Check if keys are equal
                    if(innerKey == key){
                        // Add text to element
                        document.getElementById(category).innerHTML += "<h3> " + key + " : " + values.get(key) + " </h3> "
                    }
                });
            });
        });
    }
});