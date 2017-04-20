
spa {
    name: "A Test App"
    description: "A test app"
    root: Launch
    frame: 00000000-0000-0004-0000-000000000003
    
    launch {
        name: "test page"
        description: "A test page"
        title: "Ye test page"
        frame: 00000000-0000-0004-0000-000000000003
        query: /foo
        
        foo { 
            create: by /bar {bar:/foo} query: /bar
            update: by /bar {bar:/foo} query: /bar
            delete: by /bar {bar:/foo} query: /bar
            navigate: foo by /bar {bar:/foo}
            launch: frame: 00000000-0000-0004-0000-000000000003 /bar
        }
        bar { 
            launch: by /bar 00000000-0000-0004-0000-000000000003
        }
    }
}
