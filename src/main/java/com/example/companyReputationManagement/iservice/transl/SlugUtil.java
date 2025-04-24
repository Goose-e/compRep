package com.example.companyReputationManagement.iservice.transl;

import com.ibm.icu.text.Transliterator;
import org.springframework.stereotype.Component;

@Component
public class SlugUtil {

    private final Transliterator toLatinTrans = Transliterator.getInstance("Russian-Latin/BGN");

    public String toSlug(String name) {

        name = name.replaceAll("[\"'()]", " ");
        name = name.replaceAll("[^\\p{L}\\p{Nd}\\s-]", " ");
        name = name.replace("й", "j").replace("Й", "J");
        name = name.replace("ю", "ju").replace("Ю", "JU");
        name = name.replace("е", "e").replace("Е", "E");
        name = toLatinTrans.transliterate(name);
        name = name.toLowerCase();
        name = name.trim().replaceAll("\\s+", "-");

        return name;
    }
}

