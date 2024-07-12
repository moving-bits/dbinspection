package net.movingbits.testapplication;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DataStore {

    private static final String LOGTAG = "DBInspectionSample";
    private static final String DBFILENAME = "dbinspection.sqlite";
    private static final int DBVERSION = 1;

    private static volatile SQLiteDatabase database = null;

    private static class DbHelper extends SQLiteOpenHelper {

        public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.e(LOGTAG, "on create database");
            db.execSQL("CREATE TABLE IF NOT EXISTS cities (" +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "city TEXT," +
                    "country TEXT," +
                    "popestimate INTEGER," +
                    "area INTEGER" +
                    ")");
            db.execSQL("CREATE TABLE IF NOT EXISTS mountains (" +
                    "_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "mountain TEXT," +
                    "height INTEGER," +
                    "lat TEXT," +
                    "lon TEXT," +
                    "first INTEGER," +
                    "country TEXT" +
                    ")");

            // prefill with some data
            // city data according to https://en.wikipedia.org/wiki/List_of_largest_cities
            db.execSQL("INSERT INTO cities (city, country, popestimate, area) VALUES " +
                    "('Ahmedabad', 'India', 7681000, 464)," +
                    "('Alexandria', 'Egypt', 5086000, 2300)," +
                    "('Atlanta', 'United States', 5572000, 354)," +
                    "('Baghdad', 'Iraq', 6812000, 5200)," +
                    "('Bangalore', 'India', 11440000, 709)," +
                    "('Bangkok', 'Thailand', 10156000, 1569)," +
                    "('Barcelona', 'Spain', 5494000, 101)," +
                    "('Beijing', 'China', 19618000, 16411)," +
                    "('Belo Horizonte', 'Brazil', 5972000, 331)," +
                    "('Bogotá', 'Colombia', 10574000, 1587)," +
                    "('Buenos Aires', 'Argentina', 14967000, 203)," +
                    "('Cairo', 'Egypt', 20076000, 3085)," +
                    "('Chengdu', 'China', 8813000, 14378)," +
                    "('Chennai', 'India', 10456000, 426)," +
                    "('Chicago', 'United States', 8864000, 589)," +
                    "('Chongqing', 'China', 14838000, 82403)," +
                    "('Dalian', 'China', 5300000, 13742)," +
                    "('Dallas', 'United States', 6099000, 882)," +
                    "('Dar es Salaam', 'Tanzania', 6048000, 1393)," +
                    "('Delhi', 'India', 28514000, 1484)," +
                    "('Dhaka', 'Bangladesh', 19578000, 338)," +
                    "('Dongguan', 'China', 7360000, 2465)," +
                    "('Foshan', 'China', 7236000, 3848)," +
                    "('Fukuoka', 'Japan', 5551000, 343)," +
                    "('Guadalajara', 'Mexico', 5023000, 151)," +
                    "('Guangzhou', 'China', 12638000, 7434)," +
                    "('Hangzhou', 'China', 7236000, 16596)," +
                    "('Harbin', 'China', 6115000, 53068)," +
                    "('Ho Chi Minh City', 'Vietnam', 8145000, 2061)," +
                    "('Hong Kong', 'Hong Kong', 7429000, 1104)," +
                    "('Houston', 'United States', 6115000, 1553)," +
                    "('Hyderabad', 'India', 9482000, 650)," +
                    "('Istanbul', 'Turkey', 14751000, 5196)," +
                    "('Jakarta', 'Indonesia', 10517000, 664)," +
                    "('Jinan', 'China', 5052000, 10244)," +
                    "('Johannesburg', 'South Africa', 5486000, 1643)," +
                    "('Karachi', 'Pakistan', 15400000, 3530)," +
                    "('Khartoum', 'Sudan', 5534000, 0)," +
                    "('Kinshasa', 'DR Congo', 13171000, 9965)," +
                    "('Kolkata', 'India', 15333000, 206)," +
                    "('Kuala Lumpur', 'Malaysia', 7564000, 243)," +
                    "('Lagos', 'Nigeria', 13463000, 0)," +
                    "('Lahore', 'Pakistan', 11738000, 1772)," +
                    "('Lima', 'Peru', 10391000, 2672)," +
                    "('London', 'United Kingdom', 9046000, 1572)," +
                    "('Los Angeles', 'United States', 12458000, 1214)," +
                    "('Luanda', 'Angola', 7774000, 116)," +
                    "('Madrid', 'Spain', 6497000, 606)," +
                    "('Manila', 'Philippines', 13482000, 43)," +
                    "('Mexico City', 'Mexico', 21581000, 1485)," +
                    "('Miami', 'United States', 6036000, 93)," +
                    "('Moscow', 'Russia', 12410000, 2511)," +
                    "('Mumbai', 'India', 19980000, 603)," +
                    "('Nagoya', 'Japan', 9507000, 326)," +
                    "('Nanjing', 'China', 8245000, 6582)," +
                    "('New York', 'United States', 18819000, 778)," +
                    "('Osaka', 'Japan', 19281000, 225)," +
                    "('Paris', 'France', 10901000, 105)," +
                    "('Philadelphia', 'United States', 5695000, 370)," +
                    "('Pune', 'India', 7184000, 276)," +
                    "('Qingdao', 'China', 5381000, 11229)," +
                    "('Rio de Janeiro', 'Brazil', 13293000, 1221)," +
                    "('Riyadh', 'Saudi Arabia', 6907000, 1913)," +
                    "('Saint Petersburg', 'Russia', 5383000, 1400)," +
                    "('Santiago', 'Chile', 6680000, 22)," +
                    "('São Paulo', 'Brazil', 21650000, 1521)," +
                    "('Seoul', 'South Korea', 9963000, 605)," +
                    "('Shanghai', 'China', 25582000, 6341)," +
                    "('Shenyang', 'China', 6921000, 12980)," +
                    "('Shenzhen', 'China', 11908000, 2050)," +
                    "('Singapore', 'Singapore', 5792000, 726)," +
                    "('Surat', 'India', 6564000, 327)," +
                    "('Suzhou', 'China', 6339000, 8488)," +
                    "('Tehran', 'Iran', 8896000, 751)," +
                    "('Tianjin', 'China', 13215000, 11920)," +
                    "('Tokyo', 'Japan', 37468000, 2191)," +
                    "('Toronto', 'Canada', 6082000, 630)," +
                    "('Washington', 'United States', 5207000, 177)," +
                    "('Wuhan', 'China', 8176000, 8494)," +
                    "('Xi an', 'China', 7444000, 10135)," +
                    "('Yangon', 'Myanmar', 5157000, 0)");

            // mountain data according to https://en.wikipedia.org/wiki/List_of_highest_mountains_on_Earth
            db.execSQL("INSERT INTO mountains (mountain, height, lat, lon, first, country) VALUES " +
                    "('Mount Everest Sagarmatha Chomolungma', 8849, '27°59′17″N', '86°55′30″E', 1953, 'Nepal China')," +
                    "('K2', 8611, '35°52′53″N', '76°30′48″E', 1954, 'India China')," +
                    "('Kangchenjunga', 8586, '27°42′12″N', '88°08′51″E', 1955, 'Nepal India')," +
                    "('Lhotse', 8516, '27°57′42″N', '86°55′59″E', 1956, 'China Nepal')," +
                    "('Makalu', 8485, '27°53′23″N', '87°05′20″E', 1955, 'Nepal China')," +
                    "('Cho Oyu', 8188, '28°05′39″N', '86°39′39″E', 1954, 'China Nepal')," +
                    "('Dhaulagiri I', 8167, '28°41′48″N', '83°29′35″E', 1960, 'Nepal')," +
                    "('Manaslu', 8163, '28°33′00″N', '84°33′35″E', 1956, 'Nepal')," +
                    "('Nanga Parbat', 8126, '35°14′14″N', '74°35′21″E', 1953, 'India')," +
                    "('Annapurna I', 8091, '28°35′44″N', '83°49′13″E', 1950, 'Nepal')," +
                    "('Gasherbrum I Hidden Peak K5', 8080, '35°43′28″N', '76°41′47″E', 1958, 'Pakistan China')," +
                    "('Broad Peak', 8051, '35°48′38″N', '76°34′06″E', 1957, 'Pakistan, China')," +
                    "('Gasherbrum II K4', 8035, '35°45′28″N', '76°39′12″E', 1956, 'Pakistan, China')," +
                    "('Shishapangma Gosainthan', 8027, '28°21′12″N', '85°46′43″E', 1964, 'China')," +
                    "('Gyachung Kang', 7952, '28°05′53″N', '86°44′42″E', 1964, 'Nepal China')," +
                    "('Gasherbrum III K3a', 7946, '35°45′33″N', '76°38′30″E', 1975, 'Pakistan, China')," +
                    "('Annapurna II', 7937, '28°32′05″N', '84°07′19″E', 1960, 'Nepal')," +
                    "('Gasherbrum IV K3', 7932, '35°45′38″N', '76°36′58″E', 1958, 'Pakistan'), " +
                    "('Himalchuli', 7893, '28°26′12″N', '84°38′23″E', 1960, 'Nepal')," +
                    "('Distaghil Sar', 7884, '36°19′33″N', '75°11′16″E', 1960, 'Pakistan'), " +
                    "('Ngadi Chuli', 7871, '28°30′12″N', '84°34′00″E', 1979, 'Nepal')," +
                    "('Nuptse', 7864, '27°58′03″N', '86°53′13″E', 1961, 'Nepal')," +
                    "('Khunyang Chhish', 7823, '36°12′19″N', '75°12′28″E', 1971, 'Pakistan')," +
                    "('Masherbrum K1', 7821, '35°38′28″N', '76°18′21″E', 1960, 'Pakistan')," +
                    "('Nanda Devi', 7816, '30°22′33″N', '79°58′15″E', 1936, 'India')," +
                    "('Chomo Lonzo', 7804, '27°55′50″N', '87°06′28″E', 1954, 'China'), " +
                    "('Batura Sar', 7795, '36°30′37″N', '74°31′21″E', 1976, 'Pakistan')," +
                    "('Rakaposhi', 7788, '36°08′33″N', '74°29′22″E', 1958, 'Pakistan')," +
                    "('Namcha Barwa', 7782, '29°37′52″N', '95°03′19″E', 1992, 'China')," +
                    "('Kanjut Sar', 7760, '36°12′20″N', '75°25′01″E', 1959, 'Pakistan')," +
                    "('Kamet', 7756, '30°55′12″N', '79°35′30″E', 1931, 'India')," +
                    "('Dhaulagiri II', 7751, '28°45′46″N', '83°23′18″E', 1971, 'Nepal')," +
                    "('Saltoro Kangri K10', 7742, '35°23′57″N', '76°50′53″E', 1962, 'Pakistan, India')," +
                    "('Kumbhakarna Jannu', 7711, '27°40′56″N', '88°02′40″E', 1962, 'Nepal')," +
                    "('Tirich Mir', 7708, '36°15′19″N', '71°50′30″E', 1950, 'Pakistan')," +
                    "('Molamenqing', 7703, '28°21′18″N', '85°48′35″E', 1981, 'China')," +
                    "('Gurla Mandhata', 7694, '30°26′19″N', '81°17′48″E', 1985, 'China')," +
                    "('Saser Kangri I K22', 7672, '34°52′00″N', '77°45′09″E', 1973, 'India')," +
                    "('Chogolisa', 7665, '35°36′47″N', '76°34′29″E', 1975, 'Pakistan')," +
                    "('Dhaulagiri IV', 7661, '28°44′09″N', '83°18′55″E', 1975, 'Nepal')," +
                    "('Kongur Tagh', 7649, '38°35′36″N', '75°18′48″E', 1981, 'China')," +
                    "('Dhaulagiri V', 7618, '28°44′02″N', '83°21′41″E', 1975, 'Nepal')," +
                    "('Shispare', 7611, '36°26′26″N', '74°40′51″E', 1974, 'Pakistan')," +
                    "('Trivor', 7577, '36°17′15″N', '75°05′06″E', 1960, 'Pakistan')," +
                    "('Gangkhar Puensum', 7570, '28°02′50″N', '90°27′19″E', 0, 'Bhutan China')," +
                    "('Gongga Shan Minya Konka', 7556, '29°35′43″N', '101°52′47″E', 1932, 'China')," +
                    "('Annapurna III', 7555, '28°35′06″N', '83°59′24″E', 1961, 'Nepal')," +
                    "('Skyang Kangri', 7545, '35°55′35″N', '76°34′03″E', 1976, 'Pakistan, China')," +
                    "('Changtse', 7543, '28°01′29″N', '86°54′51″E', 1982, 'China')," +
                    "('Kula Kangri', 7538, '28°13′37″N', '90°36′59″E', 1986, 'China Bhutan')," +
                    "('Kongur Tiube', 7530, '38°36′57″N', '75°11′45″E', 1956, 'China')," +
                    "('Annapurna IV', 7525, '28°32′15″N', '84°4′58″E', 1955, 'Nepal')," +
                    "('Mamostong Kangri', 7516, '35°08′31″N', '77°34′39″E', 1984, 'India')," +
                    "('Saser Kangri II E', 7513, '34°48′17″N', '77°48′24″E', 2011, 'India')," +
                    "('Muztagh Ata', 7509, '38°16′33″N', '75°06′58″E', 1956, 'China')," +
                    "('Ismoil Somoni Peak', 7495, '38°56′35″N', '72°00′57″E', 1933, 'Tajikistan')," +
                    "('Saser Kangri III', 7495, '34°50′44″N', '77°47′06″E', 1986, 'India')," +
                    "('Noshaq', 7492, '36°25′56″N', '71°49′43″E', 1960, 'Afghanistan Pakistan')," +
                    "('Pumari Chhish', 7492, '36°12′41″N', '75°15′01″E', 1979, 'Pakistan')," +
                    "('Passu Sar', 7476, '36°29′16″N', '74°35′16″E', 1994, 'Pakistan')," +
                    "('Yukshin Gardan Sar', 7469, '36°15′04″N', '75°22′29″E', 1984, 'Pakistan')," +
                    "('Teram Kangri I', 7462, '35°34′48″N', '77°04′42″E', 1975, 'China India')," +
                    "('Jongsong Peak', 7462, '27°52′54″N', '88°08′09″E', 1930, 'India China Nepal')," +
                    "('Malubiting', 7458, '36°00′12″N', '74°52′31″E', 1971, 'Pakistan')," +
                    "('Gangapurna', 7455, '28°36′18″N', '83°57′49″E', 1965, 'Nepal')," +
                    "('Jengish Chokusu Tömür Pik Pobedy', 7439, '42°02′05″N', '80°07′47″E', 1956, 'Kyrgyzstan China')," +
                    "('Sunanda Devi Nanda Devi East', 7434, '30°22′00″N', '79°59′40″E', 1939, 'India')," +
                    "('K12', 7428, '35°17′45″N', '77°01′20″E', 1974, 'Pakistan, India')," +
                    "('Yangra Ganesh I', 7422, '28°23′29″N', '85°07′38″E', 1955, 'China Nepal')," +
                    "('Sia Kangri', 7422, '35°39′48″N', '76°45′42″E', 1934, 'Pakistan China')," +
                    "('Momhil Sar', 7414, '36°19′04″N', '75°02′11″E', 1964, 'Pakistan')," +
                    "('Kabru N', 7412, '27°38′02″N', '88°07′00″E', 1994, 'India Nepal')," +
                    "('Skil Brum', 7410, '35°51′03″N', '76°25′43″E', 1957, 'Pakistan')," +
                    "('Haramosh Peak', 7409, '35°50′24″N', '74°53′51″E', 1958, 'Pakistan')," +
                    "('Istor-o-Nal', 7403, '36°22′32″N', '71°53′54″E', 1969, 'Pakistan')," +
                    "('Ghent Kangri', 7401, '35°31′04″N', '76°48′02″E', 1961, 'Pakistan, India')," +
                    "('Ultar', 7388, '36°23′27″N', '74°43′00″E', 1996, 'Pakistan')," +
                    "('Rimo I', 7385, '35°21′18″N', '77°22′08″E', 1988, 'India')," +
                    "('Churen Himal', 7385, '28°44′05″N', '83°13′03″E', 1970, 'Nepal')," +
                    "('Teram Kangri III', 7382, '35°35′59″N', '77°02′53″E', 1979, 'India China')," +
                    "('Sherpi Kangri', 7380, '35°27′58″N', '76°46′53″E', 1976, 'Pakistan')," +
                    "('Labuche Kang', 7367, '28°18′15″N', '86°21′03″E', 1987, 'China')," +
                    "('Kirat Chuli', 7362, '27°47′16″N', '88°11′43″E', 1939, 'Nepal India')," +
                    "('Abi Gamin', 7355, '30°55′57″N', '79°36′09″E', 1950, 'India China')," +
                    "('Gimmigela Chuli The Twins', 7350, '27°44′27″N', '88°09′31″E', 1994, 'India Nepal')," +
                    "('Nangpai Gosum', 7350, '28°04′24″N', '86°36′51″E', 1986, 'Nepal China')," +
                    "('Saraghrar', 7349, '36°32′51″N', '72°06′54″E', 1959, 'Pakistan')," +
                    "('Talung', 7349, '27°39′18″N', '88°07′51″E', 1964, 'Nepal India')," +
                    "('Jomolhari Chomo Lhari', 7326, '27°49′36″N', '89°16′04″E', 1937, 'Bhutan China')," +
                    "('Chamlang', 7321, '27°46′30″N', '86°58′47″E', 1961, 'Nepal')," +
                    "('Chongtar', 7315, '35°54′55″N', '76°25′45″E', 1994, 'China')," +
                    "('Baltoro Kangri', 7312, '35°38′21″N', '76°40′24″E', 1963, 'Pakistan')," +
                    "('Siguang Ri', 7309, '28°08′50″N', '86°41′06″E', 1989, 'China')," +
                    "('The Crown Huang Guan Shan', 7295, '36°06′24″N', '76°12′21″E', 1993, 'China')," +
                    "('Gyala Peri', 7294, '29°48′52″N', '94°58′07″E', 1986, 'China')," +
                    "('Porong Ri', 7292, '28°23′22″N', '85°43′12″E', 1982, 'China')," +
                    "('Baintha Brakk The Ogre', 7285, '35°56′51″N', '75°45′12″E', 1977, 'Pakistan')," +
                    "('Yutmaru Sar', 7283, '36°13′35″N', '75°22′02″E', 1980, 'Pakistan')," +
                    "('K6 Baltistan Peak', 7282, '35°25′06″N', '76°33′06″E', 1970, 'Pakistan')," +
                    "('Kangpenqing Gang Benchhen', 7281, '28°33′03″N', '85°32′44″E', 1982, 'China')," +
                    "('Muztagh Tower', 7276, '35°49′40″N', '76°21′40″E', 1956, 'Pakistan, China')," +
                    "('Mana Peak', 7272, '30°52′50″N', '79°36′55″E', 1937, 'India')," +
                    "('Dhaulagiri VI', 7268, '28°42′31″N', '83°16′27″E', 1970, 'Nepal')," +
                    "('Diran', 7266, '36°07′13″N', '74°39′42″E', 1968, 'Pakistan')," +
                    "('Labuche Kang III Labuche Kang East', 7250, '28°18′05″N', '86°23′02″E', 0, 'China')," +
                    "('Putha Hiunchuli', 7246, '28°44′52″N', '83°08′46″E', 1954, 'Nepal')," +
                    "('Apsarasas Kangri', 7245, '35°32′19″N', '77°08′55″E', 1976, 'India China')," +
                    "('Mukut Parbat', 7242, '30°56′57″N', '79°34′12″E', 1951, 'India China')," +
                    "('Rimo III', 7233, '35°22′31″N', '77°21′42″E', 1985, 'India')," +
                    "('Langtang Lirung', 7227, '28°15′22″N', '85°31′01″E', 1978, 'Nepal')," +
                    "('Karjiang', 7221, '28°15′27″N', '90°38′49″E', 0, 'China')," +
                    "('Annapurna Dakshin (Annapurna South)', 7219, '28°31′06″N', '83°48′22″E', 1964, 'Nepal')," +
                    "('Khartaphu', 7213, '28°03′49″N', '86°58′39″E', 1935, 'China')," +
                    "('Tongshanjiabu', 7207, '28°11′12″N', '89°57′27″E', 0, 'Bhutan China')," +
                    "('Malangutti Sar', 7207, '36°21′47″N', '75°08′57″E', 1985, 'Pakistan')," +
                    "('Noijin Kangsang Norin Kang', 7206, '28°56′48″N', '90°10′42″E', 1986, 'China')," +
                    "('Langtang Ri', 7205,'28°22′53″N', '85°41′01″E', 1981, 'Nepal China')," +
                    "('Kangphu Kang Shimokangri', 7204, '28°09′24″N', '90°04′15″E', 2002, 'Bhutan China')," +
                    "('Singhi Kangri', 7202, '35°35′59″N', '76°59′01″E', 1976, 'India China')," +
                    "('Lupghar Sar', 7200, '36°21′01″N', '75°02′13″E', 1979, 'Pakistan')");
            Log.e(LOGTAG, "finished onCreate");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.e(LOGTAG, "onUpgrade to version " + newVersion);
            try {
                if (db.isReadOnly()) {
                    return;
                }

                // no upgrades yet defined

            } catch (SQLiteException e) {
                Log.e(LOGTAG, "error on database upgrade: " + e.getMessage());
            }
        }
    }

    public static SQLiteDatabase getDatabase(final Context context) {
        synchronized (DataStore.class) {
            if (database != null) {
                return database;
            }
            try {
                final DbHelper dbHelper = new DbHelper(context, DBFILENAME, null, DBVERSION);
                database = dbHelper.getWritableDatabase();
            } catch (SQLiteException e) {
                Log.e(LOGTAG, "error getting database: " + e.getMessage());
            }
            return database;
        }
    }

}
