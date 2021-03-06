package com.example.cataloguemovie.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cataloguemovie.R
import com.example.cataloguemovie.databinding.FragmentMovieBinding
import com.example.cataloguemovie.movieDetail.MovieDetailFragment
import com.example.core.domain.model.Movie
import com.example.core.ui.MovieAdapter
import com.example.core.ui.MovieInterface
import com.example.core.utils.gone
import com.example.core.utils.visible
import com.example.core.vo.Resource
import org.koin.android.viewmodel.ext.android.viewModel

class MovieFragment : Fragment(), MovieInterface {
    private var movieList: ArrayList<Movie> = arrayListOf()
    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding

    private val viewModel: MovieViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getAllMovie().observe(this, { movie ->
            if (movie != null) {
                when (movie) {
                    is Resource.Success -> {
                        binding?.progressBar?.gone()
                        binding?.rvMovie?.setHasFixedSize(true)
                        binding?.rvMovie?.layoutManager = LinearLayoutManager(activity)

                        movie.data?.let {
                            movieList.addAll(it)
                            val movieAdapter = MovieAdapter(it, this)
                            binding?.rvMovie?.adapter = movieAdapter
                            movieAdapter.notifyDataSetChanged()
                        }
                    }

                    is Resource.Loading -> {
                        binding?.progressBar?.visible()
                    }

                    is Resource.Error -> {
                        binding?.errorConnection?.visible()
                        binding?.rvMovie?.gone()
                        binding?.progressBar?.gone()
                    }
                }
            }
        })
    }

    override fun click(movie: Movie) {
        val bundle = Bundle()
        bundle.putParcelable(MovieDetailFragment.MOVIE, movie)
        findNavController().navigate(R.id.movieDetailFragment, bundle)
    }

    override fun shareClick(movie: Movie) {
        activity?.let {
            val mimeType = "text/plain"
            ShareCompat.IntentBuilder
                .from(it)
                .setType(mimeType)
                .setChooserTitle(getString(R.string.send_application))
                .setText(resources.getString(R.string.share_text, movie.title))
                .startChooser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}