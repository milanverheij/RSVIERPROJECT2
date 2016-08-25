package com.adm.web.controllers;

import com.adm.database.daos.AdresDAO;
import com.adm.database.daos.KlantDAO;
import com.adm.database.service.AdresService;
import com.adm.database.service.KlantService;
import com.adm.domain.Adres;
import com.adm.domain.Klant;
import com.adm.web.forms.AdresRegisterForm;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Milan_Verheij on 19-08-16.
 *
 * Address Controller
 */

@Controller
@Component
@RequestMapping("/klant/adres")
@Transactional
@SessionAttributes("klant")
public class AdresController {
    private AdresDAO adresDAO;
    private KlantDAO klantDAO;

    @Autowired
    public AdresController(AdresService adresService, KlantService klantService) {
        this.adresDAO = adresService.getAdresDAO();
        this.klantDAO = klantService.getDAO();
    }

    @RequestMapping(value = "/register", method = GET)
    public String showRegistrationForm(Model model, Klant klant) {

        model.addAttribute("adresRegisterForm", new AdresRegisterForm());
        model.addAttribute("klant", klant);

        return "klant/adres/adresRegisterForm";
    }

    @RequestMapping(value = "/register", method = POST)
    public String processRegistration(
            @Valid AdresRegisterForm adresRegisterForm,
            Errors errors,
            Klant klant)
            throws IOException {

        if (errors.hasErrors()) {
            return "/klant/adres/adresRegisterForm";
        }

        // Maak van adersForm -> adres
        Adres nieuwAdres = adresRegisterForm.toAdres();

        // Persisten
        nieuwAdres = adresDAO.makePersistent(nieuwAdres);
        klant.getAdresGegevens().put(nieuwAdres, nieuwAdres.getType());

        // Persist alles naar de klant
        klantDAO.makePersistent(klant);

        // Redirect to created profile
        return "redirect:/klant/profile";
    }

    // Tumble status methode
    @RequestMapping(value = "/tumble/{id}", method = GET)
    public String tumbleStatusAdres(@PathVariable Long id, Klant klant, Model model) throws Exception {

        //Achtung, the id is the id of the value in the map in the client
        Adres adres = adresDAO.findById(id);

        if (adres.getAdresActief().charAt(0) == '0')
            adres.setAdresActief("1");
        else
            adres.setAdresActief("0");

        adres.setDatumGewijzigd(new Date().toString());

        // Update status of address
        adresDAO.makePersistent(adres);

        Hibernate.initialize(klant.getAdresGegevens());
        klant = klantDAO.makePersistent(klant);

        model.addAttribute("klant", klant);

        // Redirect to created profile
        return "redirect:/klant/profile";
    }

}
