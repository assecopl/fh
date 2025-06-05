package pl.fhframework.docs.forms.service;

import org.springframework.stereotype.Service;

import pl.fhframework.docs.forms.model.tv.Actor;
import pl.fhframework.docs.forms.model.tv.Country;
import pl.fhframework.docs.forms.model.tv.Episode;
import pl.fhframework.docs.forms.model.tv.Season;
import pl.fhframework.docs.forms.model.tv.TvSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Adam Zareba on 26.01.2017.
 */
@Service
public class TvSeriesServiceImpl implements TvSeriesService {

    private static final String[] COLORS = new String[]{
            "#9ddee3", "#93d8dd", "#8ad1d6", "#82cbd0", "#71c0c5", "#6ab6ba", "#62afb4", "#56a2a7", "#4c989c"
    };

    @Override
    public List<TvSeries> findAll() {
        Country usa = new Country(1l, "USA");
        Country england = new Country(2l, "England");

        Actor lenaHeadey = new Actor(1l, "Lena", "Headey");
        Actor peterDinklage = new Actor(2l, "Peter", "Dinklage");
        Actor emiliaClarke = new Actor(3l, "Emilia", "Clarke");
        Actor iainGlen = new Actor(4l, "Iain", "Glen");

        Actor bryanCranston = new Actor(5l, "Bryan", "Cranston");
        Actor aaronPaul = new Actor(6l, "Aaron", "Paul");
        Actor annaGunn = new Actor(7l, "Anna", "Gunn");
        Actor deanNorris = new Actor(8l, "Dean", "Norris");
        Actor betsyBrandt = new Actor(9l, "Betsy", "Brandt");

        Actor michaelHall = new Actor(10l, "Michael", "C. Hall");
        Actor jenniferCarpenter = new Actor(11l, "Jennifer", "Carpenter");

        Episode winterIsComing = new Episode(1l, "Winter Is Coming", "In the Seven Kingdoms of Westeros, a soldier survives an attack by the supernatural creatures known as the White Walkers.", 52.5, new ArrayList<>(Arrays.asList(lenaHeadey, peterDinklage)));
        Episode theKingsroad = new Episode(2l, "The Kingsroad", "", 53d, new ArrayList<>(Arrays.asList(lenaHeadey, peterDinklage, emiliaClarke, iainGlen)));
        Episode theNorthRemembers = new Episode(3l, "The North Remembers", "To Cersei's dismay, Tyrion takes up his post as acting Hand at King's Landing.", 60d, new ArrayList<>(Arrays.asList(peterDinklage, emiliaClarke)));
        Episode theNightLands = new Episode(4l, "The Night Lands", "Returning to his home of Pyke after nine years as the Starks' ward, Theon is reunited with his sister, Yara, and Balon, who despises Theon for his Northern ways and intends to win back his crown by force, giving the command to her.", 60d, new ArrayList<>(Arrays.asList(lenaHeadey)));
        Episode whatIsDeadMayNeverDie = new Episode(5l, "What Is Dead May Never Die", "Catelyn arrives at Renly's camp to negotiate an alliance, watching the female warrior Brienne of Tarth winning the right to join Renly's guard.", 60d, new ArrayList<>(Arrays.asList(lenaHeadey)));

        Episode pilot = new Episode(6l, "Pilot", "Walter White, a 50-year-old chemistry teacher, secretly begins making crystallized methamphetamine to support his family after learning that he has terminal lung cancer.", 45d, new ArrayList<>(Arrays.asList(bryanCranston)));
        Episode catsInTheBag = new Episode(7l, "Cat's in the Bag...", "Walt and Jesse try to dispose of the two bodies in the RV, which becomes increasingly complicated when one of them, Krazy-8, wakes up.", 48d, new ArrayList<>(Arrays.asList(bryanCranston, aaronPaul, annaGunn)));
        Episode savenThirtySeven = new Episode(8l, "Seven Thirty-Seven", "Having completed their deal with Tuco in the junkyard, Walt and Jesse realize how unhinged and violent he can become.", 45d, new ArrayList<>(Arrays.asList(deanNorris, bryanCranston)));
        Episode noMas = new Episode(9l, "No Más", "All of Albuquerque is in shock in the aftermath of the mid-air plane collision.", 45d, new ArrayList<>(Arrays.asList(bryanCranston, betsyBrandt)));
        Episode caballoSinNombre = new Episode(10l, "Caballo Sin Nombre", "Walt is having difficulty adjusting to his new life.", 45d, new ArrayList<>(Arrays.asList(bryanCranston)));
        Episode boxCutter = new Episode(11l, "Box Cutter", "Walt and Jesse are held in the lab by Victor and Mike, anxiously awaiting Gus' reaction to the murder of Gale.", 45d, new ArrayList<>(Arrays.asList(bryanCranston, aaronPaul, annaGunn, deanNorris, betsyBrandt)));

        Episode dexter = new Episode(11l, "Dexter", "Dexter Morgan (Michael C. Hall) is introduced as a serial killer who kills other killers who have escaped from, or haven't been found by the justice system.", 45d, new ArrayList<>(Arrays.asList(michaelHall, jenniferCarpenter)));

        Season gotSeason1 = new Season(1l, 1, null, new ArrayList<>(Arrays.asList(winterIsComing, theKingsroad)));
        Season gotSeason2 = new Season(2l, 2, null, new ArrayList<>(Arrays.asList(theNorthRemembers, theNightLands, whatIsDeadMayNeverDie)));

        Season bbSeason1 = new Season(3l, 1, null, new ArrayList<>(Arrays.asList(pilot, catsInTheBag)));
        Season bbSeason2 = new Season(4l, 2, null, new ArrayList<>(Arrays.asList(savenThirtySeven)));
        Season bbSeason3 = new Season(5l, 3, null, new ArrayList<>(Arrays.asList(noMas, caballoSinNombre)));
        Season bbSeason4 = new Season(6l, 4, null, new ArrayList<>(Arrays.asList(boxCutter)));

        Season dexterSeason1 = new Season(7l, 1, null, new ArrayList<>(Arrays.asList(dexter)));

        return new ArrayList<>(Arrays.asList(
                new TvSeries(1l, "Game of Thrones", new ArrayList<>(Arrays.asList(gotSeason1, gotSeason2)), usa),
                new TvSeries(2l, "Breaking Bad", new ArrayList<>(Arrays.asList(bbSeason1, bbSeason2, bbSeason3, bbSeason4)), usa),
                new TvSeries(3l, "Dexter", new ArrayList<>(Arrays.asList(dexterSeason1)), usa),
                new TvSeries(4l, "Soprano Family", null, usa),
                new TvSeries(5l, "Mr. Bean", null, england),
                new TvSeries(6l, "Silicon Valley", null, usa),
                new TvSeries(7l, "Narcos", null, usa),
                new TvSeries(8l, "Office", null, usa),
                new TvSeries(9l, "Trailer Park Boys", null, usa)
        ));
    }

    @Override
    public Map<?, String> findAllColored() {
        List<TvSeries> tvSeries = findAll();
        Map<TvSeries, String> tvSeriesMap = new HashMap<>();

        for (int i = 0; i < tvSeries.size(); i++) {
            tvSeriesMap.put(tvSeries.get(i), COLORS[i]);
        }

        return tvSeriesMap;
    }
}
