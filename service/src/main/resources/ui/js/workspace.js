var ProductModel = Backbone.Model.extend({
    
    type : "Product"
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
    products.create(new ProductModel({
        name : $("#new_product_name").val()
    }))
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