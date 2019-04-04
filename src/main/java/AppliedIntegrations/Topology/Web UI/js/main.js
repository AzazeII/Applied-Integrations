//
//  main.js
//
//  A project template for using arbor.js
//
(function($) {

    // Create renderer function
    var Renderer = function(canvas) {
        // Get canvas
        var canvas = $(canvas).get(0)

        // Get context
        var ctx = canvas.getContext("2d");

        // Get system
        var particleSystem

        var that = {
            init: function(system) {
                //
                // the particle system will call the init function once, right before the
                // first frame is to be drawn. it's a good place to set up the canvas and
                // to pass the canvas size to the particle system
                //
                // save a reference to the particle system for use in the .redraw() loop
                particleSystem = system

                // inform the system of the screen dimensions so it can map coords for us.
                // if the canvas is ever resized, screenSize should be called again with
                // the new dimensions
                particleSystem.screenSize(canvas.width, canvas.height)
                particleSystem.screenPadding(80) // leave an extra 80px of whitespace per side

                // set up some event handlers to allow for node-dragging
                that.initMouseHandling()
            },

            redraw: function() {
                //ctx.canvas.width  = window.innerWidth;
                //ctx.canvas.height = window.innerHeight;
                //
                // redraw will be called repeatedly during the run whenever the node positions
                // change. the new positions for the nodes can be accessed by looking at the
                // .p attribute of a given node. however the p.x & p.y values are in the coordinates
                // of the particle system rather than the screen. you can either map them to
                // the screen yourself, or use the convenience iterators .eachNode (and .eachEdge)
                // which allow you to step through the actual node objects but also pass an
                // x,y point in the screen's coordinate system
                //
                ctx.fillStyle = "white"
                ctx.fillRect(0, 0, canvas.width, canvas.height)

                particleSystem.eachEdge(function(edge, pt1, pt2) {
                    // edge: {source:Node, target:Node, length:#, data:{}}
                    // pt1:  {x:#, y:#}  source position in screen coords
                    // pt2:  {x:#, y:#}  target position in screen coords

                    // draw a line from pt1 to pt2
                    ctx.strokeStyle = "rgba(0,0,0, .333)"
                    ctx.lineWidth = 1
                    ctx.beginPath()
                    ctx.moveTo(pt1.x, pt1.y)
                    ctx.lineTo(pt2.x, pt2.y)
                    ctx.stroke()
                })

                particleSystem.eachNode(function(node, pt) {
                    // node: {mass:#, p:{x,y}, name:"", data:{}}
                    // pt:   {x:#, y:#}  node position in screen coords

                    // draw a rectangle centered at pt
                    var w = 10

                    ctx.fillStyle = "red"

                    if (node.name.startsWith("Co"))
                        ctx.fillStyle = "green"
                    else if (node.name.startsWith("Ou"))
                        ctx.fillStyle = "gray"
                    else if (node.name.startsWith("Wi"))
                        ctx.fillStyle = "blue"

                    ctx.fillRect(pt.x - w / 2, pt.y - w / 2, w, w)

                    ctx.fillStyle = "black"; //цвет для шрифта

                    ctx.font = 'italic 13px sans-serif'; //шрифт
                    if (node.name.startsWith("In"))
                        ctx.fillText("ME Interface", pt.x + 8, pt.y + 8);
                    else if (node.name.startsWith("Ex"))
                        ctx.fillText("ME Export Bus", pt.x + 8, pt.y + 8);
                    else if (node.name.startsWith("St"))
                        ctx.fillText("ME Storage Bus", pt.x + 8, pt.y + 8);
                    else if (node.name.startsWith("Im"))
                        ctx.fillText("ME Import Bus", pt.x + 8, pt.y + 8);
                    else if (node.name.startsWith("Ou"))
                        ctx.fillText("Outer Storage", pt.x + 8, pt.y + 8);
                    else if (node.name.startsWith("Co"))
                        ctx.fillText("ME Controller", pt.x + 8, pt.y + 8);
                    else if (node.name.startsWith("Wi"))
                        ctx.fillText("ME Cable", pt.x + 8, pt.y + 8);
                    else
                        ctx.fillText(node.name, pt.x + 8, pt.y + 8);

                })
            },

            initMouseHandling: function() {
                // no-nonsense drag and drop (thanks springy.js)
                var dragged = null;

                // set up a handler object that will initially listen for mousedowns then
                // for moves and mouseups while dragging
                var handler = {
                    clicked: function(e) {
                        var pos = $(canvas).offset();
                        _mouseP = arbor.Point(e.pageX - pos.left, e.pageY - pos.top)
                        dragged = particleSystem.nearest(_mouseP);

                        var isRightMB = false;

                        if (e.which == 3 || e.button == 2) isRightMB = true

                        if (isRightMB == false) {
                            if (dragged && dragged.node !== null) {
                                // while we're dragging, don't let physics move the node
                                dragged.node.fixed = true
                            }

                            $(canvas).bind('mousemove', handler.dragged)
                            $(window).bind('mouseup', handler.dropped)
                        } else {

                        }

                        return false
                    },

                    dragged: function(e) {
                        var pos = $(canvas).offset();
                        var s = arbor.Point(e.pageX - pos.left, e.pageY - pos.top)

                        if (dragged && dragged.node !== null) {
                            var p = particleSystem.fromScreen(s)
                            dragged.node.p = p
                        }

                        return false
                    },

                    dropped: function(e) {
                        if (dragged === null || dragged.node === undefined) return

                        dragged.node.tempMass = 1000

                        if (dragged && dragged.node !== null) {
                            dragged.node.fixed = false
                        }

                        dragged = null
                        $(canvas).unbind('mousemove', handler.dragged)
                        $(window).unbind('mouseup', handler.dropped)
                        _mouseP = null
                        return false
                    }
                }

                // start listening
                $(canvas).mousedown(handler.clicked);
                /*(function(e){

                                                                         return true;
                                                                     });*/

            },

        }
        return that
    }

    $(document).ready(function() {

        var sys = arbor.ParticleSystem(window.innerWidth, window.innerHeight, 0.5); // create the system with sensible repulsion/stiffness/friction
        sys.parameters({gravity: true}); // use center-gravity to make the graph settle nicely (ymmv)
        sys.renderer = Renderer("#viewport"); // our newly created renderer will have its .init() method called shortly by sys...

        // Get network // Create network manager
        $.getJSON("Network.json", function(network) {
            // Iterate for_each node
            $.each(network.nodes, function(i, node) {
                sys.addNode(node);
            });

            // Iterate for source length, as edge destination length is always equal
            for (var i = 0; i < network.src.length; i++){
                // Add edges
                sys.addEdge(network.src[i], network.dest[i]);
            }
        });
    })

})(this.jQuery)