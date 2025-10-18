package pl.fhframework.fhdp.example.buttons;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

public class ExampleButtonsForm extends Form<ExampleButtonsForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private int intervalText = 0;
        private Integer intervalTime = new Integer(1000);

        public String getInterText() {
            return new Integer(this.intervalText).toString();
        }

        public String testContent = "testContent";
        public String testJSON = "[{\"_id\":\"6830221863c4373f07474a3e\",\"index\":0,\"guid\":\"0c98ce1a-838c-478c-87f6-e1388c350b02\",\"isActive\":true,\"balance\":\"$2,186.26\",\"picture\":\"http://placehold.it/32x32\",\"age\":35,\"eyeColor\":\"blue\",\"name\":\"Charles Clayton\",\"gender\":\"male\",\"company\":\"EMTRAC\",\"email\":\"charlesclayton@emtrac.com\",\"phone\":\"+1 (805) 472-3054\",\"address\":\"208 Clymer Street, Fostoria, Georgia, 688\",\"about\":\"Nostrud sint nulla adipisicing culpa laborum. Sunt minim proident nulla ipsum sit et aliqua magna veniam aliquip cupidatat tempor deserunt enim. Commodo irure esse elit deserunt adipisicing non fugiat. Cillum et elit sit amet excepteur excepteur irure id veniam eu occaecat id exercitation dolor.\\r\\n\",\"registered\":\"2022-12-15T12:23:42 -01:00\",\"latitude\":-89.597349,\"longitude\":-127.408967,\"tags\":[\"reprehenderit\",\"excepteur\",\"ea\",\"amet\",\"aliquip\",\"incididunt\",\"anim\"],\"friends\":[{\"id\":0,\"name\":\"Noel Holden\"},{\"id\":1,\"name\":\"Sasha Charles\"},{\"id\":2,\"name\":\"Howell Becker\"}],\"greeting\":\"Hello, Charles Clayton! You have 6 unread messages.\",\"favoriteFruit\":\"strawberry\"},{\"_id\":\"683022181a963b36da9d7a17\",\"index\":1,\"guid\":\"46608edb-c49c-41b1-979b-8063ff7f591c\",\"isActive\":true,\"balance\":\"$1,284.58\",\"picture\":\"http://placehold.it/32x32\",\"age\":28,\"eyeColor\":\"green\",\"name\":\"Fern Stark\",\"gender\":\"female\",\"company\":\"PIVITOL\",\"email\":\"fernstark@pivitol.com\",\"phone\":\"+1 (880) 577-3808\",\"address\":\"771 Liberty Avenue, Bluffview, Maryland, 6474\",\"about\":\"Laboris minim nulla sint incididunt veniam eiusmod duis nisi magna est culpa anim anim excepteur. Dolore id ut Lorem aliquip do amet minim eiusmod labore velit aute qui. Et do enim labore sit. Consectetur et adipisicing consectetur sint ut occaecat. Ut eu ex aliquip culpa. Excepteur elit eu veniam labore laborum proident ipsum aute ut dolor enim aute eiusmod aliqua. Culpa qui nisi esse consequat ut tempor dolore sunt velit nulla labore.\\r\\n\",\"registered\":\"2017-03-03T09:12:16 -01:00\",\"latitude\":30.248762,\"longitude\":42.935448,\"tags\":[\"cupidatat\",\"sint\",\"excepteur\",\"excepteur\",\"ut\",\"ex\",\"veniam\"],\"friends\":[{\"id\":0,\"name\":\"Erna Thompson\"},{\"id\":1,\"name\":\"Benita Michael\"},{\"id\":2,\"name\":\"Tami Nunez\"}],\"greeting\":\"Hello, Fern Stark! You have 9 unread messages.\",\"favoriteFruit\":\"banana\"},{\"_id\":\"683022186dd86fbb1813442d\",\"index\":2,\"guid\":\"87e702f9-3424-4fb3-a2fd-f16e4a92cdcf\",\"isActive\":true,\"balance\":\"$1,130.09\",\"picture\":\"http://placehold.it/32x32\",\"age\":37,\"eyeColor\":\"green\",\"name\":\"Jo Bridges\",\"gender\":\"female\",\"company\":\"WRAPTURE\",\"email\":\"jobridges@wrapture.com\",\"phone\":\"+1 (867) 512-2039\",\"address\":\"653 Varick Street, Dixie, Wyoming, 8861\",\"about\":\"Anim nulla id laboris nulla. Ut excepteur labore eu nulla dolor reprehenderit eiusmod magna pariatur dolor in. Et cillum mollit exercitation voluptate magna aute nisi aute mollit minim dolor veniam anim. Excepteur consequat in anim anim ea cillum ipsum minim reprehenderit. Duis enim ipsum in cupidatat eu id cupidatat sunt velit labore ex in. Officia pariatur aute fugiat anim tempor do laborum amet est ullamco laborum.\\r\\n\",\"registered\":\"2024-04-23T02:01:27 -02:00\",\"latitude\":-14.545597,\"longitude\":-57.192428,\"tags\":[\"commodo\",\"duis\",\"reprehenderit\",\"veniam\",\"do\",\"occaecat\",\"est\"],\"friends\":[{\"id\":0,\"name\":\"Minerva Wyatt\"},{\"id\":1,\"name\":\"Kent Mendez\"},{\"id\":2,\"name\":\"Faith Erickson\"}],\"greeting\":\"Hello, Jo Bridges! You have 5 unread messages.\",\"favoriteFruit\":\"strawberry\"},{\"_id\":\"683022181769e2753137e2f2\",\"index\":3,\"guid\":\"85043163-c196-4756-8947-0768dbc4fd45\",\"isActive\":false,\"balance\":\"$2,496.94\",\"picture\":\"http://placehold.it/32x32\",\"age\":37,\"eyeColor\":\"brown\",\"name\":\"Bryant Cervantes\",\"gender\":\"male\",\"company\":\"TRIPSCH\",\"email\":\"bryantcervantes@tripsch.com\",\"phone\":\"+1 (890) 511-2859\",\"address\":\"536 Vandalia Avenue, Catherine, Wisconsin, 4450\",\"about\":\"Nisi fugiat officia sint reprehenderit adipisicing fugiat dolore minim sit et proident. Est culpa ad excepteur do tempor. Sunt amet adipisicing ea velit et qui esse ad aliquip. Tempor et sit ex proident velit Lorem ad. Anim duis dolore dolor nostrud ipsum consectetur laboris reprehenderit non aliqua. Minim sit anim labore in do aliquip consequat do aliquip pariatur aliqua mollit nisi qui.\\r\\n\",\"registered\":\"2015-06-16T02:45:21 -02:00\",\"latitude\":-60.612521,\"longitude\":29.232242,\"tags\":[\"culpa\",\"in\",\"non\",\"ipsum\",\"culpa\",\"consectetur\",\"velit\"],\"friends\":[{\"id\":0,\"name\":\"Patterson Montoya\"},{\"id\":1,\"name\":\"Snow Rhodes\"},{\"id\":2,\"name\":\"Oliver Atkinson\"}],\"greeting\":\"Hello, Bryant Cervantes! You have 10 unread messages.\",\"favoriteFruit\":\"apple\"},{\"_id\":\"68302218df24e4f475b4dca7\",\"index\":4,\"guid\":\"b6f5f6ae-8f7c-4db9-b0d2-180dacb0ad7a\",\"isActive\":false,\"balance\":\"$3,325.23\",\"picture\":\"http://placehold.it/32x32\",\"age\":27,\"eyeColor\":\"green\",\"name\":\"Carmella Whitaker\",\"gender\":\"female\",\"company\":\"ISODRIVE\",\"email\":\"carmellawhitaker@isodrive.com\",\"phone\":\"+1 (928) 527-2097\",\"address\":\"378 Murdock Court, Carlos, New York, 3998\",\"about\":\"Labore labore culpa laboris veniam pariatur aute velit nulla. Ad commodo reprehenderit cillum Lorem ipsum duis fugiat reprehenderit reprehenderit ipsum ipsum ipsum laborum laborum. Deserunt aute deserunt proident elit exercitation aliquip duis mollit id. Amet laboris nulla amet enim adipisicing voluptate ipsum. Ullamco id pariatur irure commodo culpa excepteur quis occaecat ex ipsum commodo ex labore et. Labore non dolore officia non ullamco aliquip ullamco. Cillum incididunt ea tempor nisi dolor sunt sit duis occaecat.\\r\\n\",\"registered\":\"2023-06-19T08:58:54 -02:00\",\"latitude\":22.427172,\"longitude\":-75.117345,\"tags\":[\"nostrud\",\"Lorem\",\"exercitation\",\"ad\",\"labore\",\"enim\",\"Lorem\"],\"friends\":[{\"id\":0,\"name\":\"Ericka David\"},{\"id\":1,\"name\":\"Kristen Bond\"},{\"id\":2,\"name\":\"Edwina Noel\"}],\"greeting\":\"Hello, Carmella Whitaker! You have 8 unread messages.\",\"favoriteFruit\":\"apple\"}]";
        public String testXML = "<root>\n" +
                "  <perhaps>graph</perhaps>\n" +
                "  <dollar>\n" +
                "    <orange>-464307188.737731</orange>\n" +
                "    <cage>distant</cage>\n" +
                "    <greatly>\n" +
                "      <position>\n" +
                "        <near>\n" +
                "          <sometime>\n" +
                "            <perhaps>fed</perhaps>\n" +
                "            <radio>percent</radio>\n" +
                "            <somebody>effect</somebody>\n" +
                "            <seldom>\n" +
                "              <method>\n" +
                "                <political>250540845.1901014</political>\n" +
                "                <thousand>visitor</thousand>\n" +
                "                <glad>art</glad>\n" +
                "                <nine>-1850151312</nine>\n" +
                "                <kitchen>lost</kitchen>\n" +
                "                <care>hello</care>\n" +
                "                <theory>statement</theory>\n" +
                "                <scale>ring</scale>\n" +
                "                <captured>clear</captured>\n" +
                "                <stairs>-1016831749</stairs>\n" +
                "                <struggle>near</struggle>\n" +
                "                <fact>pan</fact>\n" +
                "              </method>\n" +
                "              <limited>rapidly</limited>\n" +
                "              <perfectly>muscle</perfectly>\n" +
                "              <flower>press</flower>\n" +
                "              <luck>1529532863.4001825</luck>\n" +
                "              <solution>rhyme</solution>\n" +
                "              <she>673358596.5309508</she>\n" +
                "              <discuss>-8662589</discuss>\n" +
                "              <similar>-243657053</similar>\n" +
                "              <story>smaller</story>\n" +
                "              <private>song</private>\n" +
                "            </seldom>\n" +
                "            <angle>661167610</angle>\n" +
                "            <written>program</written>\n" +
                "            <moving>1401393145.6353276</moving>\n" +
                "            <increase>cast</increase>\n" +
                "            <ask>2048686801.9198754</ask>\n" +
                "            <huge>taught</huge>\n" +
                "            <son>66283935</son>\n" +
                "            <contrast>into</contrast>\n" +
                "          </sometime>\n" +
                "          <lie>1631154290</lie>\n" +
                "          <store>weight</store>\n" +
                "          <means>1594006552</means>\n" +
                "          <closely>484843158</closely>\n" +
                "          <speech>sing</speech>\n" +
                "          <using>youth</using>\n" +
                "          <send>loud</send>\n" +
                "          <cotton>-786008301.6376426</cotton>\n" +
                "          <recent>afraid</recent>\n" +
                "          <easier>secret</easier>\n" +
                "          <press>-1738016339.0272064</press>\n" +
                "        </near>\n" +
                "        <pipe>-1600914033.4459274</pipe>\n" +
                "        <flow>-1944588580.8766875</flow>\n" +
                "        <breath>then</breath>\n" +
                "        <safety>maybe</safety>\n" +
                "        <wooden>believed</wooden>\n" +
                "        <condition>choose</condition>\n" +
                "        <lot>-1073021718</lot>\n" +
                "        <might>occasionally</might>\n" +
                "        <fine>leave</fine>\n" +
                "        <mean>-1250649646.8153865</mean>\n" +
                "        <bag>-213703082.06164074</bag>\n" +
                "      </position>\n" +
                "      <smaller>difficulty</smaller>\n" +
                "      <body>341105232.55368614</body>\n" +
                "      <pitch>-207473487</pitch>\n" +
                "      <neighbor>-45856733.91321325</neighbor>\n" +
                "      <molecular>bear</molecular>\n" +
                "      <fewer>trail</fewer>\n" +
                "      <naturally>gasoline</naturally>\n" +
                "      <pipe>-1908824771</pipe>\n" +
                "      <tea>customs</tea>\n" +
                "      <cent>-1803022311</cent>\n" +
                "      <saw>write</saw>\n" +
                "    </greatly>\n" +
                "    <perfectly>yard</perfectly>\n" +
                "    <solar>summer</solar>\n" +
                "    <been>strength</been>\n" +
                "    <into>surrounded</into>\n" +
                "    <creature>species</creature>\n" +
                "    <frame>-293978595.84234595</frame>\n" +
                "    <pick>nose</pick>\n" +
                "    <variety>from</variety>\n" +
                "    <later>temperature</later>\n" +
                "  </dollar>\n" +
                "  <excellent>-1953698641.1563866</excellent>\n" +
                "  <girl>-1159336006.1714509</girl>\n" +
                "  <yet>-1359706598</yet>\n" +
                "  <women>188156094</women>\n" +
                "  <substance>belt</substance>\n" +
                "  <wood>master</wood>\n" +
                "  <nothing>395323860</nothing>\n" +
                "  <star>great</star>\n" +
                "  <flow>garden</flow>\n" +
                "  <curious>1863164137.5385613</curious>\n" +
                "</root>";
        public String testMode = "TEXT";
    }

}
