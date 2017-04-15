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
            title: 'Applications'
            query: '{ workspaces { id name description } }'
            
        };
    
    edges {
        WorkspaceExplorer.IsA.SinglePageApp 
        AllWorkspaces.IsA.Page 
        
        AllWorkspaces.frame.KernelWorkspace
        
        WorkspaceExplorer.hasPage.AllWorkspaces
            attribute values {
                route: 'workspaces'
            }
    } 
}
>>

