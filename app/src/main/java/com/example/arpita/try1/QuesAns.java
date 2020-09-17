package com.example.arpita.try1;

import java.util.ArrayList;

/**
 * Created by arpita on 11/08/16.
 */
public class QuesAns {

    public static class Faq{
        String ques;
        String  ans;

        public Faq(String ques, String ans) {
            this.ques = ques;
            this.ans = ans;
        }
    }

    public static ArrayList<Faq> getQ (){
        ArrayList<Faq> faqs=new ArrayList<>(10);

            faqs.add(new Faq("What does this app do? ", "OneStop is an app designed for easy access of nearby places. Using this app, one can" +
                    " find places nearby to him/her along with place details. The user can also find directions for reaching a particular place" +
                    " or search for his/her favourite place"));

        faqs.add(new Faq("How to use the app? ", "The app automatically fetches your current location if your GPS is enabled and shows your location" +
                " on the map. The user can now select the type of places he/she wants to search for from the navigation drawer, the app would then " +
                "show all the nearby places of interest on the map. If you want to look up for a particular place on the map, you can click on " +
                "the window above the required marker, it would show all the details of that place including the address and contact no. " +
                "  You can also navigate away to that place using the \"GET DIRECTION\" button. "));

        faqs.add(new Faq("Can I look up a particular place? ", " Yes, U can search for your place of interest using the search feature, the app" +
                " would display the place on the map and on clicking you can know all about that place "));

        faqs.add(new Faq("Can I see all my places of interest at one place ?", " Yes, U can see all your places at one place to select from ." +
                " Once you have selected a category, you would see a list button on the map, clicking it would redirect you to a list that shows " +
                "  all the places of interest nearby you. From the list ,you can then select anyone and look it up."));


        return faqs;


    }
}
