package com.stretching.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.bignerdranch.expandablerecyclerview.ChildViewHolder
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter
import com.bignerdranch.expandablerecyclerview.ParentViewHolder
import com.bignerdranch.expandablerecyclerview.model.Parent
import com.stretching.R
import com.stretching.databinding.ItemHomeChildBinding
import com.stretching.databinding.ItemHomeParentBinding
import com.stretching.objects.HomeTrainingPlans


class HomePlansExpandableAdapter : ExpandableRecyclerAdapter<HomeTrainingPlans.MainPlan, HomeTrainingPlans.Plan, HomePlansExpandableAdapter.GrpViewHolder, HomePlansExpandableAdapter.ItemViewHolder> {

    private var mInflater: LayoutInflater? = null
    private var context: Context? = null
    private var mEventListener: EventListener? = null
    private var isMultiSelect = false


    constructor(
        context: Context?,
        groups: List<HomeTrainingPlans.MainPlan?>
    ) : super(groups) {
        this.context = context
        getParentList().clear()
        getParentList().addAll(groups)
        mInflater = LayoutInflater.from(context)
    }

    fun getAll(): List<HomeTrainingPlans.MainPlan?>? {
        return getParentList()
    }

    fun getMenuItem(position: Int): HomeTrainingPlans.MainPlan? {
        return getParentList().get(position)
    }

    fun getMenuSubItem(position: Int, child: Int): HomeTrainingPlans.Plan? {
        return getParentList().get(position).getChildList().get(child)
    }

    fun addAll(groups: List<HomeTrainingPlans.MainPlan?>) {
        getParentList().clear()
        getParentList().addAll(groups)
        notifyParentDataSetChanged(false)
    }

    fun setMultiSelect(multiSelect: Boolean) {
        isMultiSelect = multiSelect
    }


    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }

    override fun getItemCount(): Int {
        return super.getItemCount()
    }

    override fun onCreateParentViewHolder(
        parentViewGroup: ViewGroup,
        viewType: Int
    ): GrpViewHolder {
        val inflater = LayoutInflater.from(parentViewGroup.context)

        val rowSideMenuBinding = DataBindingUtil.inflate<ItemHomeParentBinding>(
            inflater,
            R.layout.item_home_parent, parentViewGroup, false
        )

        return GrpViewHolder(rowSideMenuBinding)
    }

    override fun onCreateChildViewHolder(childViewGroup: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(childViewGroup.context)

        val rowSideMenuBinding = DataBindingUtil.inflate<ItemHomeChildBinding>(
            inflater,
            R.layout.item_home_child, childViewGroup, false
        )

        return ItemViewHolder(rowSideMenuBinding)
    }

    override fun onBindParentViewHolder(
        recipeViewHolder: GrpViewHolder, parentPosition: Int,
        recipe: HomeTrainingPlans.MainPlan
    ) {

        recipeViewHolder.parentBinding.container.setOnClickListener {

        }
        recipeViewHolder.parentBinding.tvName.text = recipe.planName
    }

    override fun onBindChildViewHolder(
        ingredientViewHolder: ItemViewHolder,
        parentPosition: Int, childPosition: Int,
        ingredient: HomeTrainingPlans.Plan
    ) {

        ingredientViewHolder.childBinding.tvTitle.text = ingredient.title
        ingredientViewHolder.childBinding.imgCover.setImageResource(ingredient.coverImg!!)

        if(ingredient.isPro) {
            ingredientViewHolder.childBinding.imgPro.visibility = View.VISIBLE
        }else{
            ingredientViewHolder.childBinding.imgPro.visibility = View.GONE
        }

        if(ingredient.des.isNullOrEmpty().not()) {
            ingredientViewHolder.childBinding.tvDes.visibility = View.VISIBLE
            ingredientViewHolder.childBinding.tvDes.text = ingredient.des
        }else{
            ingredientViewHolder.childBinding.tvDes.visibility = View.GONE
        }

        if (ingredient.workoutTime > 0 || ingredient.totalWorkOut > 0) {
            ingredientViewHolder.childBinding.llTimeWorkOut.visibility = View.VISIBLE
            ingredientViewHolder.childBinding.tvWorkoutTime.text = "${ingredient.workoutTime} "+ context!!.getString(R.string.minutes)
            ingredientViewHolder.childBinding.tvTotalWorkout.text = "${ingredient.totalWorkOut} "+context!!.getString(R.string.workouts)
        }else{
            ingredientViewHolder.childBinding.llTimeWorkOut.visibility = View.GONE
        }

        if(ingredient.isGoShow) {
            ingredientViewHolder.childBinding.tvGo.visibility = View.VISIBLE
        }else{
            ingredientViewHolder.childBinding.tvGo.visibility = View.GONE
        }

        ingredientViewHolder.childBinding.container.setOnClickListener(View.OnClickListener {
            if (mEventListener != null) {
                mEventListener!!.OnMenuClick(parentPosition, childPosition)
            }
        })
    }

    fun getItem(position: Int): HomeTrainingPlans.MainPlan? {
        return getParentList().get(position)
    }

    class GrpViewHolder(val parentBinding: ItemHomeParentBinding) :
        ParentViewHolder<Parent<Any?>, Any?>(parentBinding.root) {
    }


    class ItemViewHolder(val childBinding: ItemHomeChildBinding) :
        ChildViewHolder<Any?>(childBinding.root) {
    }


    interface EventListener {
        fun OnMenuClick(parentPosition: Int, childPosition: Int)
    }

    fun setEventListener(eventlistener: EventListener?) {
        mEventListener = eventlistener
    }
}