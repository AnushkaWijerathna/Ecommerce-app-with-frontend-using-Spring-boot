package com.university.ecom_app.controller;

import com.university.ecom_app.model.Product;
import com.university.ecom_app.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
//Using cross-origin annotation resolves front end backend server crash or CORS error
@CrossOrigin
@RequestMapping("/api")
public class ProductController {

    private ProductService service;


    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String greet(){
        return "Hello world";
    }

    @GetMapping("/home")
    public String goHome() {
        return "Welcome to the E-Commerce API Home!";
    }
    //Use "ResponseEntity<>" to Controll the status code when sending HttpRequest

    //get all products
    @GetMapping("/products")
    public List<Product> getAllProducts(){
        return service.getAllProducts();
    }

    //Get the product which the user clicks in the home page
    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id){

        Product product = service.getProductById(id);

        if(product != null)
            return new ResponseEntity<>(product, HttpStatus.OK);

        else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    //@RequestPart in Spring Boot is used to get a specific part of a multipart/form-data request, usually when uploading files(ex :- image files) along with other data.
    //<?> means the response can contain any type of object. MultipartFile is used in Spring to handle uploaded files.
    @PostMapping("/product")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile imageFile){

        try {
            Product product1 = service.addProduct(product, imageFile);
            return new ResponseEntity<>(product1, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Also check if the product is available as well.
    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getImageById(@PathVariable int id){

        Product product = service.getProductById(id);
        //This is a clean, built-in Spring method that constructs a response with an HTTP status of 404 (Not Found) and an empty body.
        if (product == null){
            return ResponseEntity.notFound().build();
        }

        //Get image in byte[] type
        else {
            byte[] imageFile = product.getImageData();
            //Send the image with its type and imageFile body
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(product.getImageType()))
                    .body(imageFile);
        }

    }

    /**
     * Updates an existing product's details and its image at the same time.
     * * @param id        Gets the product ID from the URL.
     * @param product   Gets the updated text data (name, price, description, etc.).
     * @param imageFile Gets the newly uploaded binary image file.
     */
    @PutMapping("/product/{id}")
    public ResponseEntity<String>updateProduct(@PathVariable int id,@RequestPart Product product, @RequestPart MultipartFile imageFile) throws IOException {

        Product product1 = null;
        try {
            product1 = service.updateProducts(id, product, imageFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (product1 != null)
            return new ResponseEntity<>("Update successfull", HttpStatus.OK);

        else
            return new ResponseEntity<>("Failed to update Product", HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String>deleteProduct(@PathVariable int id){

        Product product1 = service.getProductById(id);

        if (product1 != null) {
            service.deleteProduct(id);
            return new ResponseEntity<>("Product delete", HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity<>("Product deleting failed", HttpStatus.OK);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>>searchByKeyword(@RequestParam("keyword") String keyword){

        System.out.println("Searching with "+ keyword);
        List<Product> product = service.searchByKeyword(keyword);
        return new ResponseEntity<>(product,HttpStatus.OK);
    }

    @GetMapping("/csrf-token")
    public CsrfToken getCsrf(HttpServletRequest request){

        return (CsrfToken) request.getAttribute("_csrf");
    }
}
