
spa {
    name: "A Test App"
    description: "A test app"
    root: launch
    frame: 00000000-0000-0004-0000-000000000003
    
    launch {
        name: "test page"
        description: "A test page"
        title: "Ye test page"
        frame: 00000000-0000-0004-0000-000000000003
        query: 'wsp/allWorkspaces.query'
        
        workspaces { 
            create: by /bar {bar:/foo} query: 'wsp/workspaceDetail.query'
            update: by /bar {bar:/foo} query: 'wsp/workspaceDetail.query'
            delete: by /bar/baz {bar:/foo} query: 'wsp/workspaceDetail.query'
            navigate: workspace by /id meta {id:/id}
        }
        imports { 
            launch: by /id 00000000-0000-0004-0000-000000000003
        }
    }
    
    workspace {
        name: "Workspace Detail"
        description: "Detail for workspace"
        title: "Ye workspace detail"
        query: 'wsp/workspaceDetail.query'
    }
}
