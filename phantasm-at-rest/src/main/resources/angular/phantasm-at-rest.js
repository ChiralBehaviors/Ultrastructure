var phantasm = angular.module('phantasm', [ 'restangular' ]);

phantasm.config([ "RestangularProvider", function(RestangularProvider) {
	RestangularProvider.setBaseUrl("/json-ld");
} ]);

phantasm.factory("Facet", [ "Restangular", function(Restangular) {
	var service = Restangular.service("facet");
	return service;
} ]);

phantasm.factory("WorkspaceMediated", [ "Restangular", function(Restangular) {
	var service = Restangular.service("workspace-mediated");
	return service;
} ]);

phantasm.service("PhantasmRelative", function() {
	this.translateIdToFacetInstance = function(inst) {
		inst["@id"] = this.instance(inst["@id"]);
	};

	this.facet = function(path) {
		var split = path.split('/');
		var newPath = '';
		var slice = split.slice(split.length - 3, split.length);
		for ( var i in slice) {
			if (i > 0) {
				newPath = newPath + '/' + slice[i];
			} else {
				newPath = slice[i];
			}
		}
		return newPath;
	};
	this.instance = function(path) {
		return path.substr(path.lastIndexOf('/') + 1);
	};
	this.facetInstance = function(path) {
		var split = path.split('/');
		var newPath = '';
		var slice = split.slice(split.length - 3, split.length);
		for ( var i in slice) {
			if (i > 0) {
				newPath = newPath + '/' + slice[i];
			} else {
				newPath = slice[i];
			}
		}
		return newPath;
	};
	this.fullyQualifiedInstance = function(path) {
		var split = path.split('/');
		var newPath = '';
		var slice = split.slice(split.length - 4, split.length);
		for ( var i in slice) {
			if (i > 0) {
				newPath = newPath + '/' + slice[i];
			} else {
				newPath = slice[i];
			}
		}
		return newPath;
	};
	this.facetInstances = function(path) {
		var split = path.split('/');
		var newPath = '';
		var slice = split.slice(split.length - 4, split.length);
		for ( var i in slice) {
			if (i > 0) {
				newPath = newPath + '/' + slice[i];
			} else {
				newPath = slice[i];
			}
		}
		return newPath;
	};
});

phantasm.factory("Phantasm", [
		"Facet",
		function(Facet) {
			var phantasm = {};
			phantasm.facetInstance = function(ruleform, classifier,
					classification, instance) {
				var facet = this.facet(ruleform, classifier, classification);
				return facet.one(instance);
			};
			phantasm.facet = function(ruleform, classifier, classification) {
				return Facet.one(ruleform).one(classifier).one(classification);
			};
			phantasm.facetInstances = function(ruleform, classifier,
					classification) {
				var facet = this.facet(ruleform, classifier, classification);
				return facet.one("instances");
			};
			return phantasm;
		} ]);

phantasm.factory("WorkspacePhantasm", [
		"WorkspaceMediated",
		function(WorkspaceMediated) {
			var workspacePhantasm = {};
			workspacePhantasm.facet = function(workspace, ruleform, classifier,
					classification) {
				return WorkspaceMediated.one(workspace).one("facet").one(
						ruleform).one(classifier).one(classification);
			};
			workspacePhantasm.facetInstance = function(workspace, ruleform,
					classifier, classification, instance) {
				var facet = this.facet(workspace, ruleform, classifier,
						classification);
				return facet.one(instance);
			};
			workspacePhantasm.facetInstances = function(workspace, ruleform,
					classifier, classification) {
				var facet = this.facet(workspace, ruleform, classifier,
						classification);
				return facet.one("instances");
			};
			return workspacePhantasm;
		} ]);