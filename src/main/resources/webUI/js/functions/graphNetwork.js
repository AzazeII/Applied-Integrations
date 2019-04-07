// Map of sub network grids
var sub_networkList = [];

// Create message array
var nodeMessages = [];

// Create vis node set
var nodes = new vis.DataSet();

// create an array with edges
var edges = new vis.DataSet();

// Current mode
var mode;

function graph(network){
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

        // Change color of node
        visNode.color = "#" + network.data[i].Hex.toString(16);

        // Check if color is code of transparent color
        if(network.data[i].Hex == 9002152)
            // Change color to color of fluix crystal
            visNode.color = "#343161";

        // Change shape to dot
        visNode.shape = "dot";

        // Change id of node
        visNode.hashLabel = node;

        // Add vis node
        nodes.add(visNode)

        // Check if mode is sub network mode
        if (network.mode == "sub_network"){
            // Put grid object in sub network grid map
            sub_networkList.push(network.iGridData[i]);
        }

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

    // Switch mode
    mode = network.mode;
}