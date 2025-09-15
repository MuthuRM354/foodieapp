package com.foodieapp.user.controller;

import com.foodieapp.user.dto.request.AddressRequest;
import com.foodieapp.user.dto.response.AddressDTO;
import com.foodieapp.user.dto.response.ApiResponse;
import com.foodieapp.user.model.User;
import com.foodieapp.user.service.address.AddressService;
import com.foodieapp.user.util.ResponseUtil;
import com.foodieapp.user.util.TokenUtil;
import com.foodieapp.user.validation.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@Tag(name = "Address Management", description = "APIs for managing user addresses")
public class AddressController {

    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);

    private final AddressService addressService;
    private final TokenUtil tokenUtil;

    @Autowired
    public AddressController(AddressService addressService, TokenUtil tokenUtil) {
        this.addressService = addressService;
        this.tokenUtil = tokenUtil;
    }

    @PostMapping
    @Operation(
            summary = "Create a new address",
            description = "Add a new address to the user's profile"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Address created successfully",
                    content = @Content(schema = @Schema(implementation = AddressDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid address data"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            )
    })
    public ResponseEntity<ApiResponse<AddressDTO>> createAddress(
            @RequestHeader("Authorization") String authHeader,
            @Validated({ValidationGroups.Create.class, Default.class}) @RequestBody AddressRequest addressRequest) {

        User user = tokenUtil.getUserFromToken(authHeader);
        logger.info("Creating new address for user: {}", user.getId());

        AddressDTO createdAddress = addressService.createAddress(user, addressRequest);
        return ResponseUtil.success("Address created successfully", createdAddress);
    }

    @GetMapping
    @Operation(
            summary = "Get all addresses",
            description = "Retrieve all addresses for the authenticated user"
    )
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAllAddresses(
            @RequestHeader("Authorization") String authHeader) {

        User user = tokenUtil.getUserFromToken(authHeader);
        logger.info("Retrieving all addresses for user: {}", user.getId());

        List<AddressDTO> addresses = addressService.getAllAddressesByUser(user);
        return ResponseUtil.success("Addresses retrieved successfully", addresses);
    }

    @GetMapping("/{addressId}")
    @Operation(
            summary = "Get address by ID",
            description = "Retrieve a specific address by its ID"
    )
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "ID of the address to retrieve")
            @PathVariable String addressId) {

        User user = tokenUtil.getUserFromToken(authHeader);
        logger.info("Retrieving address {} for user: {}", addressId, user.getId());

        AddressDTO address = addressService.getAddress(user, addressId);
        return ResponseUtil.success("Address retrieved successfully", address);
    }

    @PutMapping("/{addressId}")
    @Operation(
            summary = "Update address",
            description = "Update an existing address"
    )
    public ResponseEntity<ApiResponse<AddressDTO>> updateAddress(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "ID of the address to update")
            @PathVariable String addressId,
            @Validated({ValidationGroups.Update.class, Default.class}) @RequestBody AddressRequest addressRequest) {

        User user = tokenUtil.getUserFromToken(authHeader);
        logger.info("Updating address {} for user: {}", addressId, user.getId());

        AddressDTO updatedAddress = addressService.updateAddress(user, addressId, addressRequest);
        return ResponseUtil.success("Address updated successfully", updatedAddress);
    }

    @DeleteMapping("/{addressId}")
    @Operation(
            summary = "Delete address",
            description = "Delete an address by its ID"
    )
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "ID of the address to delete")
            @PathVariable String addressId) {

        User user = tokenUtil.getUserFromToken(authHeader);
        logger.info("Deleting address {} for user: {}", addressId, user.getId());

        addressService.deleteAddress(user, addressId);
        return ResponseUtil.success("Address deleted successfully");
    }

    @PostMapping("/{addressId}/default")
    @Operation(
            summary = "Set default address",
            description = "Set an address as the default address"
    )
    public ResponseEntity<ApiResponse<AddressDTO>> setDefaultAddress(
            @RequestHeader("Authorization") String authHeader,
            @Parameter(description = "ID of the address to set as default")
            @PathVariable String addressId) {

        User user = tokenUtil.getUserFromToken(authHeader);
        logger.info("Setting address {} as default for user: {}", addressId, user.getId());

        AddressDTO defaultAddress = addressService.setDefaultAddress(user, addressId);
        return ResponseUtil.success("Address set as default successfully", defaultAddress);
    }
}
