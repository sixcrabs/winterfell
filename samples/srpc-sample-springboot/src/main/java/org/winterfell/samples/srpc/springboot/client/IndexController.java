package org.winterfell.samples.srpc.springboot.client;

import org.winterfell.samples.srpc.api.PersonService;
import org.winterfell.srpc.starter.annotation.RpcInject;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * .
 * </p>
 *
 * @author alex
 * @version v1.0, 2019/12/23
 */
@CrossOrigin
@RestController
@RequestMapping("/api")
public class IndexController {

    @RpcInject
    private PersonService personService;


    @GetMapping("/plus/{a}/{b}")
    public String getPlus(@PathVariable String a, @PathVariable String b) {
        return personService.plus(a, b);
    }

}
