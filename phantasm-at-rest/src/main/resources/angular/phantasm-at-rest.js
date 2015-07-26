var phantasm = angular.module('phantasm', [ 'restangular' ]);

phantasm.config([ "RestangularProvider", function(RestangularProvider) {
	RestangularProvider.setBaseUrl("/json-ld");
} ]);

phantasm.factory("Facet", [ "Restangular", function(Restangular) {
	var service = Restangular.service("facet");
	return service;
} ]);

phantasm.factory("WorkspaceFacet", [ "Restangular", function(Restangular) {
	var service = Restangular.service("workspace-mediated/facet");
	return service;
} ]);

phantasm.service("Phantasm", [
		"Facet",
		function(Facet) {
			this.facetInstance = function(ruleform, classifier, classification,
					instance) {
				return this.facet(ruleform, classifier, classification).one(
						instance);
			};
			this.facet = function(ruleform, classifier, classification) {
				return Facet.one(ruleform).one(classifier).one(classification);
			};
		} ]);

phantasm.service("WorkspacePhantasm", [
		"WorkspaceFacet",
		function(WorkspaceFacet) {
			this.facetInstance = function(workspace, ruleform, classifier,
					classification, instance) {
				return this.facet(workspace, ruleform, classifier,
						classification).one(instance);
			};
			this.facet = function(workspace, ruleform, classifier,
					classification) {
				return WorkspaceFacet.one(ruleform).one(classifier).one(
						classification);
			};
		} ]);