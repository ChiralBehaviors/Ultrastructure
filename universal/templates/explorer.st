explorer(resources) ::= <<
workspace: 'uri:http://ultrastructure.me/ontology/com.chiralbehaviors/uaas/workspace-explorer'
           version: 1
           'UaaS Workspace Explorer' 'A browser/ide for Ultrastructure workspaces'
imports {
    'urn:uuid:00000000-0000-0004-0000-000000000003' as kernel 
    'uri:http://ultrastructure.me/ontology/com.chiralbehaviors/uaas/single-page' as spa
}

products {
    WorkspaceExplorer = 'Workspace Explorer' 'A browser/ide for Ultrastructure workspaces'
        attribute values {
            name : 'Workspace Explorer' 
            description: 'A browser/ide for Ultrastructure workspaces'
            root: 'workspaces'
        };
        
    AllWorkspaces = 'All workspaces' 'Page with all workspaces'
        attribute values {
            name: 'All workspaces'
            description: 'Page with all workspaces'
            title: 'Workspaces'
            query: '{ workspaces { id _ext { name description } } }'
        };
        
    WorkspaceDetail = 'Workspace Detail' 'Detail for a single workspace'
        attribute values {
            name: 'Workspace Detail'
            description: 'Detail for a single workspace'
            title: 'Workspace'
            query: '<resources.workspaceDetail>'
        };
        
    GimmeWorkspace = 'workspace detail rout' 'Launch the selected application'
    
    edges {
        WorkspaceExplorer.IsA.SinglePageApp 
        AllWorkspaces.IsA.Page
        WorkspaceDetail.IsA.Page
        
        GimmeWorkspace.IsA.Route
            attribute values {
                name: 'GimmeWorkspace'
                description: 'Route to workspace detail'
                route: 'workspace'
            };
        
        WorkspaceExplorer.hasPage.AllWorkspaces
            attribute values {
                route: 'workspaces'
            }

        WorkspaceExplorer.hasPage.WorkspaceDetail
            attribute values {
                route: 'workspace'
            }

        WorkspaceDetail.navigate.GimmeWorkspace
            attribute values {
                relation: 'workspaces'
            }
    } 
}
>>

