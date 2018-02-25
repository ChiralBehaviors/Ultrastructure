
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
        style: '{"labels" : {"/workspaces/imports/_edge":"imports"} }'
        
        workspaces {
            navigate: workspaces by /id meta {id:/id}
        }
        imports { 
            launch: by /id 00000000-0000-0004-0000-000000000003
        }
    }
    
    workspaces {
        name: "Workspace Detail"
        description: "Detail for workspace"
        title: "Ye workspace detail"
        query: 'wsp/workspaceDetail.query'
        
        facets {
            navigate: facet meta {id:/id}
        }
        
        agencies {
            navigate: agency meta {id:/id}
        }
        
        attributes {
            navigate: attribute meta {id:/id}
        }
        
        intervals {
            navigate: interval meta {id:/id}
        }
        
        locations {
            navigate: location meta {id:/id}
        }
        
        products {
            navigate: product meta {id:/id}
        }
        
        relationships {
            navigate: relationship meta {id:/id}
        }
        
        statusCodes {
            navigate: statusCode meta {id:/id}
        }
        
        units {
            navigate: unit meta {id:/id}
        }
    }
    
    facet {
        name: "Facet Detail"
        description: "Detail for a facet"
        title: "Ye facet detail"
        query: 'wsp/facetDetail.query'
        style: '{"labels" : {"/facet/attributes/authorizedAttribute":"attributes"} }'
        
        authorizedAttribute {
            navigate: attribute meta {id:/id}
        }
        
        classifier {
            navigate: relationship meta {id:/id}
        }
        
        classification {
            navigate: existential meta {id:/id}
        }
        
        parent {
            navigate: facet meta {id:/id}
        }
        
        relationship {
            navigate: relationship meta {id:/id}
        }
        
        child {
            navigate: facet meta {id:/id}
        }
    }
        
    agency {
        name: "Agency Detail"
        description: "Detail for agency"
        title: "Ye agency detail"
        query: 'wsp/agencyDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    attribute {
        name: "Attribute Detail"
        description: "Detail for attribute"
        title: "Ye attribute detail"
        query: 'wsp/attributeDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    interval {
        name: "Interval Detail"
        description: "Detail for interval"
        title: "Ye interval detail"
        query: 'wsp/intervalDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    location {
        name: "Location Detail"
        description: "Detail for location"
        title: "Ye location detail"
        query: 'wsp/locationDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    product {
        name: "Product Detail"
        description: "Detail for product"
        title: "Ye product detail"
        query: 'wsp/productDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    relationship {
        name: "Relationship Detail"
        description: "Detail for relationship"
        title: "Ye relationship detail"
        query: 'wsp/relationshipDetail.query'
        
        inverse {
            navigate: relationship meta {id:/id}
        }
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    statusCode {
        name: "Status Code Detail"
        description: "Detail for status codes"
        title: "Ye status code detail"
        query: 'wsp/statusCodeDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    unit {
        name: "Unit Detail"
        description: "Detail for unit"
        title: "Ye unit detail"
        query: 'wsp/unitDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
        
    existential {
        name: "Existential Detail"
        description: "Detail for existential"
        title: "Ye existential detail"
        query: 'wsp/existentialDetail.query'
        
        workspace {
            navigate: workspace by /id meta {id:/id}
        }
    }
}
