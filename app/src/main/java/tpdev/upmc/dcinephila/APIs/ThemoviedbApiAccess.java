package tpdev.upmc.dcinephila.APIs;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ThemoviedbApiAccess {


	public static String TheMovieDbAPIKey = "api_key=7bf8c81d05dcc42e3cc3216950eafc2d";

	public static String MOVIE_DETAILS = "https://api.themoviedb.org/3/movie/";
	public static String TVSHOW_DETAILS = "https://api.themoviedb.org/3/tv/";
	public static String PERSON_DETAILS = "https://api.themoviedb.org/3/person/";

	public static String NOW_PALYING_MOVIES = "https://api.themoviedb.org/3/movie/now_playing?api_key=7bf8c81d05dcc42e3cc3216950eafc2d&language=fr";
	public static String UPCOMING_MOVIES = "https://api.themoviedb.org/3/movie/upcoming?api_key=7bf8c81d05dcc42e3cc3216950eafc2d&language=fr";
	public static String TOP_RATED_MOVIES = "https://api.themoviedb.org/3/movie/top_rated?api_key=7bf8c81d05dcc42e3cc3216950eafc2d";
	public static String MOST_POPULAR_TVSHOWS = "https://api.themoviedb.org/3/tv/popular?sort_by=vote_average.desc&api_key=7bf8c81d05dcc42e3cc3216950eafc2d";
	public static String TOP_RATED_TVSHOWS = "https://api.themoviedb.org/3/tv/top_rated?api_key=7bf8c81d05dcc42e3cc3216950eafc2d&sort_by=vote_average.desc";
	public static String ON_THE_AIR_TVSHOWS = "https://api.themoviedb.org/3/tv/on_the_air?api_key=7bf8c81d05dcc42e3cc3216950eafc2d";
	public static String MOVIES_BY_GENRE = "https://api.themoviedb.org/3/genre/";

	public static String MULTIPLE_SEARCH = "https://api.themoviedb.org/3/search/multi?api_key=7bf8c81d05dcc42e3cc3216950eafc2d&language=fr&query=";

	public static String ILE_DE_FRANCE_CINEMAS = "https://data.iledefrance.fr/api/records/1.0/search/?dataset=les_salles_de_cinemas_en_ile-de-france&rows=309";
	

	public static StringBuffer GetResponseFromAPI(String string_url) throws Exception
	{
		String inputLine;
		URL url = new URL(string_url);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(connection.getInputStream()));
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response; 
	}
	
	
	public static String AllPersonDetailsURL(int id_person)
	{
		return PERSON_DETAILS + String.valueOf(id_person) + "?" + TheMovieDbAPIKey ;
	}


	public static String PersonMovies(int id_person)
	{
		return PERSON_DETAILS + String.valueOf(id_person) + "/movie_credits?" + TheMovieDbAPIKey ;
	}

	public static String PersonShows(int id_person)
	{
		return PERSON_DETAILS + String.valueOf(id_person) + "/tv_credits?" + TheMovieDbAPIKey ;
	}

	public static String PersonImages(int id_person)
	{
		return PERSON_DETAILS + String.valueOf(id_person) + "/images?" + TheMovieDbAPIKey ;
	}

	public static String GetArticlesQuery(String query)
	{
		return "https://newsapi.org/v2/everything?q="+query+"&sources=mtv-news&apiKey=88e8854c17764c1aa395eec3f361984c";
	}
		
	public static String AllMovieDetailsURL(int id_movie)
	{
		return MOVIE_DETAILS + String.valueOf(id_movie) + "?append_to_response=credits&language=fr&" + TheMovieDbAPIKey ;
	}

	public static String MovieBackdrops(int id_movie)
	{
		return MOVIE_DETAILS + String.valueOf(id_movie) + "/images?" + TheMovieDbAPIKey ;
	}

	public static String TvShowsBackdrops(int id_show)
	{
		return TVSHOW_DETAILS+ String.valueOf(id_show) + "/images?" + TheMovieDbAPIKey ;
	}
	
	public static  String MovieRecommendationsURl(int id_movie)
	{
		return MOVIE_DETAILS + String.valueOf(id_movie) + "/similar?" + TheMovieDbAPIKey ;
	}
	
	public static  String MoviesByGenre(int id_genre)
	{
		return MOVIES_BY_GENRE + String.valueOf(id_genre) + "/movies?" + TheMovieDbAPIKey ; 
	}
	
	public static String GetMovieTrailer(int id_movie)
	{
		return MOVIE_DETAILS + String.valueOf(id_movie) + "/videos?&" + TheMovieDbAPIKey ;
	}
	
	public static String AllTvShowDetailsURL(int id_tvshow)
	{
		return TVSHOW_DETAILS + String.valueOf(id_tvshow) + "?append_to_response=seasons,credits&language=fr&" + TheMovieDbAPIKey ;
	}
	
	public static  String TvShowSimilarURl(int id_tvshow)
	{
		return TVSHOW_DETAILS + String.valueOf(id_tvshow) + "/similar?" + TheMovieDbAPIKey ;
	}
	
	
	public static String GetTvShowTrailer(int id_tvshow)
	{
		return TVSHOW_DETAILS + String.valueOf(id_tvshow) + "/videos?&" + TheMovieDbAPIKey ;
	}

	public static  String GetSeasonEpisodes(int id_show, int season_number)
	{
		return TVSHOW_DETAILS + String.valueOf(id_show) + "/season/" + String.valueOf(season_number) +"?language=fr&" + TheMovieDbAPIKey ;
	}
	
	public static String GetMultipleSearchResult(String search_query)
	{
		return MULTIPLE_SEARCH + search_query; 
	}
	
}
