var network;
var w = 500;
var h = 500;
var r = 10;

var nodes = [];
var links = [];

var counter = 800;

var colors = d3.scale.category10();
function tick() {
	node.attr("cx", function(d) {
		d.x = Math.max(r, Math.min(w - r, d.x));
        return d.x;
	}).attr("cy", function(d) {
		d.y = Math.max(r, Math.min(h - r, d.y));
        return d.y;
	});

	link.attr("x1", function(d) {
		return d.source.x;
	}).attr("y1", function(d) {
		return d.source.y;
	}).attr("x2", function(d) {
		return d.target.x;
	}).attr("y2", function(d) {
		return d.target.y;
	});
}

var force = d3.layout.force()
                .size([ w, h ])
                .linkDistance([ 50 ])
                .charge([ -100 ])
                .on("tick", tick);

//create the SVG element
var svg = d3.select("body")
			.append("svg")
			.attr("width", w)
			.attr("height", h)
			.on("dblclick", function() {
				var n = {name: "new node", id: ++counter};
				nodes.push(n);
				start();
			});

//append the marker definition to the SVG element
//this is necessary for the arrow heads on the directed edges
svg.append("svg:defs")
	.append("svg:marker")
	.attr("id", "marker")
	.attr("viewBox", "0 -5 10 10")
	.attr("refX", 15)
	.attr("refY", -1.5)
	.attr("markerWidth", 6)
	.attr("markerHeight", 6)
	.attr("orient", "auto")
	.append("svg:path")
	.attr("d", "M0,-5L10,0L0,5");

var node = svg.selectAll(".node"), 
	link = svg.selectAll(".link");

function start() {
	link = link.data(force.links());
	link.enter()
		.insert("line", ".node")
		.attr("class", "link");
	link.exit()
		.remove();

	node = node.data(force.nodes(), function(d) {
		return d.id;
	});
	node.enter()
		.append("circle")
		.attr("class", function(d) {
			return "node " + d.id;
		})
		.attr("r", r).call(force.drag);
	node.exit().remove();

	force.start();
}


$.getJSON("http://localhost:8080/v1/services/data/collection/2?rel=15",
		function(dataset) {
			$.each(dataset.nodes, function(d, i) {
				nodes.push(i);
			});
			$.each(dataset.edges, function(d, i) {
				links.push(i);
			});
			console.log(nodes);
			console.log(links);
			force.nodes(nodes);
			force.links(links);
			start();

		});