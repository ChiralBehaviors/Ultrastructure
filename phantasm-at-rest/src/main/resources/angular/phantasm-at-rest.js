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

phantasm.factory("Phantasm", [
		"Facet",
		function(Facet) {
			var phantasm = {};
			phantasm.facetInstance = function(ruleform, classifier,
					classification, instance) {
				return this.facet(ruleform, classifier, classification).one(
						instance);
			};
			phantasm.facet = function(ruleform, classifier, classification) {
				return Facet.one(ruleform).one(classifier).one(classification);
			};
			phantasm.facetInstances = function(ruleform, classifier,
					classification) {
				return this.facet(ruleform, classifier, classification).one(
						"instances");
			};
			return phantasm;
		} ]);

phantasm.factory("WorkspacePhantasm", [
		"WorkspaceMediated",
		function(WorkspaceMediated) {
			var workspacePhantasm = {};
			workspacePhantasm.facetInstance = function(workspace, ruleform,
					classifier, classification, instance) {
				return this.facet(workspace, ruleform, classifier,
						classification).one(instance);
			};
			workspacePhantasm.facet = function(workspace, ruleform, classifier,
					classification) {
				return WorkspaceMediated.one(workspace).one("facet").one(
						ruleform).one(classifier).one(classification);
			};
			workspacePhantasm.facetInstances = function(workspace, ruleform,
					classifier, classification) {
				return this.facet(workspace, ruleform, classifier,
						classification).one("instances");
			};
			return workspacePhantasm;
		} ]);