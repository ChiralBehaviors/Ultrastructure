
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
        query: 'wsp/workspaceDetail.query'
        
        foo { 
            create: by /bar {bar:/foo} query: 'wsp/workspaceDetail.query'
            update: by /bar {bar:/foo} query: 'wsp/workspaceDetail.query'
            delete: by /bar/baz {bar:/foo} query: 'wsp/workspaceDetail.query'
            navigate: foo by /bar {bar:/foo}
            launch: frame: 00000000-0000-0004-0000-000000000003 /bar
        }
        bar { 
            launch: by /bar 00000000-0000-0004-0000-000000000003
        }
    }
}
