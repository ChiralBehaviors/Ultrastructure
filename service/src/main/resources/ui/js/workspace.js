var ProductModel = Backbone.Model.extend({  
    url: "/v1/services/data/collection/6",
    type : "product"
});

var ProductCollection = Backbone.Collection.extend({
    model: ProductModel,
    url: "/v1/services/data/collection/6"
})
//product: 6
//relationship: 32
var ProductView = Backbone.View.extend({
    tagName : "li",
    render : function() {
        alert(this.model.get("name"));
    }
})

var products = new ProductCollection();

$("#submit").click(function() {
    console.log("name: " + $("#new_product_name"));
    var p = new ProductModel({
        name : $("#new_product_name").val(),
        type: "Product"
    });
    console.log(p);
    p.save(p);
})

$('#find').click(function() {
    console.log("find");
    var r = new ProductModel({
        id : 6,
    });

    r.fetch({
        success : function(model) {
            var view = new ProductView({
                el : "product-list",
                model : model
            });
            view.render();
        }
    });
});