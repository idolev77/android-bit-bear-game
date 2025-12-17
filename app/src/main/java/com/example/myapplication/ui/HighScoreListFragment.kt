package com.example.myapplication.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.data.HighScore
import com.example.myapplication.data.HighScoreManager
import com.example.myapplication.databinding.FragmentHighScoreListBinding

/**
 * HighScoreListFragment - displays a RecyclerView with the top 10 high scores
 * Part 2: Clicking an item notifies the parent activity to zoom the map
 */
class HighScoreListFragment : Fragment() {

    private var _binding: FragmentHighScoreListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: HighScoreAdapter
    private var listener: OnScoreSelectedListener? = null

    interface OnScoreSelectedListener {
        fun onScoreSelected(highScore: HighScore)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnScoreSelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHighScoreListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        loadHighScores()
    }

    private fun setupRecyclerView() {
        adapter = HighScoreAdapter { highScore ->
            listener?.onScoreSelected(highScore)
        }
        binding.recyclerHighScores.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerHighScores.adapter = adapter
    }

    private fun loadHighScores() {
        val highScoreManager = HighScoreManager(requireContext())
        val scores = highScoreManager.getHighScores()

        if (scores.isEmpty()) {
            binding.textNoScores.visibility = View.VISIBLE
            binding.recyclerHighScores.visibility = View.GONE
        } else {
            binding.textNoScores.visibility = View.GONE
            binding.recyclerHighScores.visibility = View.VISIBLE
            adapter.submitList(scores)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}

