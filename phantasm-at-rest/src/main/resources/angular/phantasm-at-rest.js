var phantasm = angular.module('phantasm', [ 'restangular' ]);

phantasm.config([ "RestangularProvider", function(RestangularProvider) {
	RestangularProvider.setBaseUrl("/json-ld");
} ]);

phantasm.factory("Facet", [ "Restangular", function(Restangular) {
	var service = Restangular.service("facet");
	return service;
} ]);

phantasm.factory("RuleformResource", [ "Restangular", function(Restangular) {
	var service = Restangular.service("ruleform");
	return service;
} ]);

phantasm.factory("WorkspaceMediated", [ "Restangular", function(Restangular) {
	var service = Restangular.service("workspace-mediated");
	return service;
} ]);

phantasm.factory("Ruleform", [ "RuleformResource", function(RuleformResource) {
	var ruleform = {};
	ruleform.ruleform = function(ruleform) {
		return RuleformResource.one(ruleform);
	};
	ruleform.instance = function(ruleform, instance) {
		var form = this.ruleform(ruleform);
		return form.one(instance);
	};
	ruleform.instances = function(ruleform) {
		var form = this.ruleform(ruleform);
		return form.one("instances");
	};
	return ruleform;
} ]);

phantasm.factory("Phantasm", [
		"Facet",
		function(Facet) {
			var phantasm = {};
			phantasm.facet = function(ruleform, classifier, classification) {
				return Facet.one(ruleform).one(classifier).one(classification);
			};
			phantasm.facetInstance = function(ruleform, classifier,
					classification, instance) {
				var facet = this.facet(ruleform, classifier, classification);
				return facet.one(instance);
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