
spa {
    name: "A Test App"
    description: "A test app"
    root: Launch
    
    launch {
        name: "test page"
        title: "Ye test page"
        query: /foo
        
        foo { 
            create: by /bar {bar:/foo} query: /bar
            navigate: foo {bar:/foo}
            launch: 00000000-0000-0004-0000-000000000003
        }
    }
}
