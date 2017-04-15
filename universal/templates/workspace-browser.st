workspace-browser(resources) ::= <<
workspace: 'uri:http://ultrastructure.me/ontology/com.chiralbehaviors/uaas/workspace-browser'
           version: 1
           'UaaS Workspace Browser' 'A browser/ide for Ultrastructure workspaces'
imports {
    'urn:uuid:00000000-0000-0004-0000-000000000003' as kernel {
            IsA.Workspace
    }
}

products {
    WorkspaceBrowser = 'Workspace Browser' 'A browser/ide for Ultrastructure workspaces'
        attribute values {
            name : 'Workspace Browser'
            description: 'A browser/ide for Ultrastructure workspaces'
            root: 'allWorkspaces'
        };
        
    AllWorkspacess = 'All workspaces' 'Page with all workspaces'
        attribute values {
            name: 'All workspaces'
            description: 'Page with all workspaces'
            title: 'Applications'
            query: '{ workspaces { id name description } }'
            
        };
    LaunchApplication = 'Launch Application' 'Launch the selected application'
        attribute values {
            name: 'Launch Application'
            description: 'Launch the selected application'
            launchBy: 'id'
        };
    
    edges {
        AppLauncher.IsA.SinglePageApp 
        AllApplications.IsA.Page
        LaunchApplication.IsA.Launch
        
        AppLauncher.hasPage.AllApplications
            attribute values {
                route: 'launch'
            }
        
        AllApplications.launch.LaunchApplication
            attribute values {
                relation: 'singlePageApplications'
            }
    }

    facets {
        IsA.SinglePageApp { name description root }
            constraints {
                one frame: IsA.Workspace named by relationship 
                n hasPage: IsA.Page { route }
            }

        IsA.Page { name description query title }
            constraints {
                one pageOf: IsA.SinglePageApp
                one frame: IsA.Workspace named by relationship 

                n create: IsA.Action named by relationship { relation }
                n update: IsA.Action named by relationship { relation }
                n delete: IsA.Action named by relationship { relation }
                n navigate: IsA.Route named by relationship { relation }
                n launch: IsA.Launch named by relationship { relation }
            }

        IsA.Route { frameBy extract path }

        IsA.Action { frameBy extract query }

        IsA.Launch { frameBy launchBy }
            constraints {
                one launchOf: IsA.Page
                one frame: IsA.Workspace named by relationship
                one launch: IsA.SinglePageApp named Immediate
            }
    }
}
relationships {
    hasPage = 'Has Page' 'A has page B' | pageOf = 'Page Of' 'A is a page of B';
    hasRoute = 'Has Route' 'A has route B' | routeOf = 'Route Of' 'A is a route of B';
    frame = 'A is framed by the workspace B' | frameOf = 'A is the workspace frame of page B';
    create = 'A creates an instance of the relation using action B' | createActionOf = 'A is an action used to create a new instance of a relation of page B';
    delete = 'A deletes the relation using the action B' | deleteActionOf = 'A is an action used to delete a relation of page B';
    update = 'A updates relation using route B' | updateActionOf = 'A is a route used to update a relation of page B';
    navigate = 'A navigates the relation using route B' | navigationOf = 'A is a route used to navigate a relation of page B';
    launch = 'A launches the application using the application launch defined by B' 
                | launchOf = 'A is a launch used to launch an application from a relation of page B';
}
>>

