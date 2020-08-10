package kalmanTracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import embryoDetector.Embryoobject;
import net.imglib2.algorithm.MultiThreaded;




public class EmbryoobjectCollection implements MultiThreaded
{

	public static final Double ZERO = Double.valueOf( 0d );

	public static final Double ONE = Double.valueOf( 1d );

	public static final String VISIBLITY = "VISIBILITY";

	/**
	 * Time units for filtering and cropping operation timeouts. Filtering
	 * should not take more than 1 minute.
	 */
	private static final TimeUnit TIME_OUT_UNITS = TimeUnit.MINUTES;

	/**
	 * Time for filtering and cropping operation timeouts. Filtering should not
	 * take more than 1 minute.
	 */
	private static final long TIME_OUT_DELAY = 1;

	/** The Time by Time list of Embryoobject this object wrap. */
	private ConcurrentSkipListMap< String, Set< Embryoobject > > content = new ConcurrentSkipListMap< >();

	private int numThreads;

	/*
	 * CONSTRUCTORS
	 */

	/**
	 * Construct a new empty Embryoobject collection.
	 */
	public EmbryoobjectCollection()
	{
		setNumThreads();
	}

	/*
	 * METHODS
	 */

	/**
	 * Retrieves and returns the {@link Embryoobject} object in this collection with the
	 * specified ID. Returns <code>null</code> if the Embryoobject cannot be found. All
	 * ThreeDRoiobjects, visible or not, are searched for.
	 *
	 * @param ID
	 *            the ID to look for.
	 * @return the Embryoobject with the specified ID or <code>null</code> if this Embryoobject
	 *         does not exist or does not belong to this collection.
	 */
	public Embryoobject search( final int ID )
	{
		Embryoobject Embryoobject = null;
		for ( final Embryoobject s : iterable( false ) )
		{
			if ( s.ID() == ID )
			{
				Embryoobject = s;
				break;
			}
		}
		return Embryoobject;
	}

	@Override
	public String toString()
	{
		String str = super.toString();
		str += ": contains " + getNThreeDRoiobjects( false ) + " ThreeDRoiobjects total in " + keySet().size() + " different Times, over which " + getNThreeDRoiobjects( true ) + " are visible:\n";
		for ( final String key : content.keySet() )
		{
			str += "\tTime " + key + ": " + getNThreeDRoiobjects( key, false ) + " ThreeDRoiobjects total, " + getNThreeDRoiobjects( key, true ) + " visible.\n";
		}
		return str;
	}

	/**
	 * Adds the given Embryoobject to this collection, at the specified Time, and mark
	 * it as visible.
	 * <p>
	 * If the Time does not exist yet in the collection, it is created and
	 * added. Upon adding, the added Embryoobject has its feature {@link Embryoobject#Time}
	 * updated with the passed Time value.
	 * 
	 * @param Embryoobject
	 *            the Embryoobject to add.
	 * @param Time
	 *            the Time to add it to.
	 */
	public void add( final Embryoobject Embryoobject, final String Time )
	{
		Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects )
		{
			ThreeDRoiobjects = new HashSet< >();
			content.put( Time, ThreeDRoiobjects );
		}
		ThreeDRoiobjects.add( Embryoobject );
		Embryoobject.putFeature( Embryoobject.POSITION_T, Double.valueOf( Time ) );
		Embryoobject.putFeature( VISIBLITY, ONE );
	}

	/**
	 * Removes the given Embryoobject from this collection, at the specified Time.
	 * <p>
	 * If the Embryoobject Time collection does not exist yet, nothing is done and
	 * <code>false</code> is returned. If the Embryoobject cannot be found in the Time
	 * content, nothing is done and <code>false</code> is returned.
	 * 
	 * @param Embryoobject
	 *            the Embryoobject to remove.
	 * @param Time
	 *            the Time to remove it from.
	 * @return <code>true</code> if the Embryoobject was succesfully removed.
	 */
	public boolean remove( final Embryoobject Embryoobject, final Integer Time )
	{
		final Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects ) { return false; }
		return ThreeDRoiobjects.remove( Embryoobject );
	}

	/**
	 * Marks all the content of this collection as visible or invisible.
	 *
	 * @param visible
	 *            if true, all ThreeDRoiobjects will be marked as visible.
	 */
	public void setVisible( final boolean visible )
	{
		final Double val = visible ? ONE : ZERO;
		final Collection< String > Times = content.keySet();

		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );
		for ( final String Time : Times )
		{

			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{

					final Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
					for ( final Embryoobject Embryoobject : ThreeDRoiobjects )
					{
						Embryoobject.putFeature( VISIBLITY, val );
					}

				}
			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.setVisible()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
	}

	

	

	/**
	 * Returns the closest {@link Embryoobject} to the given location (encoded as a
	 * Embryoobject), contained in the Time <code>Time</code>. If the Time has no
	 * Embryoobject, return <code>null</code>.
	 *
	 * @param location
	 *            the location to search for.
	 * @param Time
	 *            the Time to inspect.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only search though visible ThreeDRoiobjects. If false, will
	 *            search through all ThreeDRoiobjects.
	 * @return the closest Embryoobject to the specified location, member of this
	 *         collection.
	 */
	public final Embryoobject getClosestThreeDRoiobject( final Embryoobject location, final int Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects )
			return null;
		double d2;
		double minDist = Double.POSITIVE_INFINITY;
		Embryoobject target = null;
		for ( final Embryoobject s : ThreeDRoiobjects )
		{

			if ( visibleThreeDRoiobjectsOnly && ( s.getFeature( VISIBLITY ).compareTo( ZERO ) <= 0 ) )
			{
				continue;
			}

			d2 = s.squareDistanceTo( location );
			if ( d2 < minDist )
			{
				minDist = d2;
				target = s;
			}

		}
		return target;
	}

	/**
	 * Returns the {@link Embryoobject} at the given location (encoded as a Embryoobject),
	 * contained in the Time <code>Time</code>. A Embryoobject is returned <b>only</b>
	 * if there exists a Embryoobject such that the given location is within the Embryoobject
	 * radius. Otherwise <code>null</code> is returned.
	 *
	 * @param location
	 *            the location to search for.
	 * @param Time
	 *            the Time to inspect.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only search though visible ThreeDRoiobjects. If false, will
	 *            search through all ThreeDRoiobjects.
	 * @return the closest Embryoobject such that the specified location is within its
	 *         radius, member of this collection, or <code>null</code> is such a
	 *         ThreeDRoiobjects cannot be found.
	 */
	public final Embryoobject getThreeDRoiobjectAt( final Embryoobject location, final int Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects || ThreeDRoiobjects.isEmpty() ) { return null; }

		final TreeMap< Double, Embryoobject > distanceToThreeDRoiobject = new TreeMap< >();
		double d2;
		for ( final Embryoobject s : ThreeDRoiobjects )
		{
			if ( visibleThreeDRoiobjectsOnly && ( s.getFeature( VISIBLITY ).compareTo( ZERO ) <= 0 ) )
				continue;

			d2 = s.squareDistanceTo( location );
				distanceToThreeDRoiobject.put( d2, s );
		}
		if ( distanceToThreeDRoiobject.isEmpty() )
			return null;

		return distanceToThreeDRoiobject.firstEntry().getValue();
	}
	
	/**
	 * Returns the <code>n</code> closest {@link Embryoobject} to the given location
	 * (encoded as a Embryoobject), contained in the Time <code>Time</code>. If the
	 * number of ThreeDRoiobjects in the Time is exhausted, a shorter list is returned.
	 * <p>
	 * The list is ordered by increasing distance to the given location.
	 *
	 * @param location
	 *            the location to search for.
	 * @param Time
	 *            the Time to inspect.
	 * @param n
	 *            the number of ThreeDRoiobjects to search for.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only search though visible ThreeDRoiobjects. If false, will
	 *            search through all ThreeDRoiobjects.
	 * @return a new list, with of at most <code>n</code> ThreeDRoiobjects, ordered by
	 *         increasing distance from the specified location.
	 */
	public final List< Embryoobject > getNClosestThreeDRoiobjects( final Embryoobject location, final int Time, int n, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
		final TreeMap< Double, Embryoobject > distanceToThreeDRoiobject = new TreeMap< >();

		double d2;
		for ( final Embryoobject s : ThreeDRoiobjects )
		{

			if ( visibleThreeDRoiobjectsOnly && ( s.getFeature( VISIBLITY ).compareTo( ZERO ) <= 0 ) )
			{
				continue;
			}

			d2 = s.squareDistanceTo( location );
			distanceToThreeDRoiobject.put( d2, s );
		}

		final List< Embryoobject > selectedThreeDRoiobjects = new ArrayList< >( n );
		final Iterator< Double > it = distanceToThreeDRoiobject.keySet().iterator();
		while ( n > 0 && it.hasNext() )
		{
			selectedThreeDRoiobjects.add( distanceToThreeDRoiobject.get( it.next() ) );
			n--;
		}
		return selectedThreeDRoiobjects;
	}

	/**
	 * Returns the total number of ThreeDRoiobjects in this collection, over all Times.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only count visible ThreeDRoiobjects. If false count all
	 *            ThreeDRoiobjects.
	 * @return the total number of ThreeDRoiobjects in this collection.
	 */
	public final int getNThreeDRoiobjects( final boolean visibleThreeDRoiobjectsOnly )
	{
		int nThreeDRoiobjects = 0;
		if ( visibleThreeDRoiobjectsOnly )
		{

			final Iterator< Embryoobject > it = iterator( true );
			while ( it.hasNext() )
			{
				it.next();
				nThreeDRoiobjects++;
			}

		}
		else
		{

			for ( final Set< Embryoobject > ThreeDRoiobjects : content.values() )
				nThreeDRoiobjects += ThreeDRoiobjects.size();
		}
		return nThreeDRoiobjects;
	}

	/**
	 * Returns the number of ThreeDRoiobjects at the given Time.
	 *
	 * @param Time
	 *            the Time.
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, will only count visible ThreeDRoiobjects. If false count all
	 *            ThreeDRoiobjects.
	 * @return the number of ThreeDRoiobjects at the given Time.
	 */
	public int getNThreeDRoiobjects( final String Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		if ( visibleThreeDRoiobjectsOnly )
		{
			final Iterator< Embryoobject > it = iterator( Time, true );
			int nThreeDRoiobjects = 0;
			while ( it.hasNext() )
			{
				it.next();
				nThreeDRoiobjects++;
			}
			return nThreeDRoiobjects;
		}

		final Set< Embryoobject > ThreeDRoiobjects = content.get( Time );
		if ( null == ThreeDRoiobjects )
			return 0;
		
		return ThreeDRoiobjects.size();
	}

	/*
	 * FEATURES
	 */

	/**
	 * Builds and returns a new map of feature values for this Embryoobject collection.
	 * Each feature maps a double array, with 1 element per {@link Embryoobject}, all
	 * pooled together.
	 *
	 * @param features
	 *            the features to collect
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible Embryoobject values will be
	 *            collected.
	 * @return a new map instance.
	 */
	public Map< String, double[] > collectValues( final Collection< String > features, final boolean visibleOnly )
	{
		final Map< String, double[] > featureValues = new ConcurrentHashMap< >( features.size() );
		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );

		for ( final String feature : features )
		{
			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{
					final double[] values = collectValues( feature, visibleOnly );
					featureValues.put( feature, values );
				}

			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.collectValues()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached while filtering." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}

		return featureValues;
	}

	/**
	 * Returns the feature values of this Embryoobject collection as a new double array.
	 * <p>
	 * If some ThreeDRoiobjects do not have the interrogated feature set (stored value is
	 * <code>null</code>) or if the value is {@link Double#NaN}, they are
	 * skipped. The returned array might be therefore of smaller size than the
	 * number of ThreeDRoiobjects interrogated.
	 *
	 * @param feature
	 *            the feature to collect.
	 * @param visibleOnly
	 *            if <code>true</code>, only the visible Embryoobject values will be
	 *            collected.
	 * @return a new <code>double</code> array.
	 */
	public final double[] collectValues( final String feature, final boolean visibleOnly )
	{
		final double[] values = new double[ getNThreeDRoiobjects( visibleOnly ) ];
		int index = 0;
		for ( final Embryoobject Embryoobject : iterable( visibleOnly ) )
		{
			final Double feat = Embryoobject.getFeature( feature );
			if ( null == feat )
			{
				continue;
			}
			final double val = feat.doubleValue();
			if ( Double.isNaN( val ) )
			{
				continue;
			}
			values[ index ] = val;
			index++;
		}
		return values;
	}

	/*
	 * ITERABLE & co
	 */

	/**
	 * Return an iterator that iterates over all the ThreeDRoiobjects contained in this
	 * collection.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the returned iterator will only iterate through
	 *            visible ThreeDRoiobjects. If false, it will iterate over all ThreeDRoiobjects.
	 * @return an iterator that iterates over this collection.
	 */
	public Iterator< Embryoobject > iterator( final boolean visibleThreeDRoiobjectsOnly )
	{
		if ( visibleThreeDRoiobjectsOnly )
			return new VisibleThreeDRoiobjectsIterator();

		return new AllThreeDRoiobjectsIterator();
	}

	/**
	 * Return an iterator that iterates over the ThreeDRoiobjects in the specified Time.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the returned iterator will only iterate through
	 *            visible ThreeDRoiobjects. If false, it will iterate over all ThreeDRoiobjects.
	 * @param Time
	 *            the Time to iterate over.
	 * @return an iterator that iterates over the content of a Time of this
	 *         collection.
	 */
	public Iterator< Embryoobject > iterator( final String Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		final Set< Embryoobject > TimeContent = content.get( Time );
		if ( null == TimeContent ) { return EMPTY_ITERATOR; }
		if ( visibleThreeDRoiobjectsOnly )
			return new VisibleThreeDRoiobjectsTimeIterator( TimeContent );

		return TimeContent.iterator();
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for this
	 * collection as a whole.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the iterable will contains only visible ThreeDRoiobjects.
	 *            Otherwise, it will contain all the ThreeDRoiobjects.
	 * @return an iterable view of this Embryoobject collection.
	 */
	public Iterable< Embryoobject > iterable( final boolean visibleThreeDRoiobjectsOnly )
	{
		return new WholeCollectionIterable( visibleThreeDRoiobjectsOnly );
	}

	/**
	 * A convenience methods that returns an {@link Iterable} wrapper for a
	 * specific Time of this Embryoobject collection. The iterable is backed-up by the
	 * actual collection content, so modifying it can have unexpected results.
	 *
	 * @param visibleThreeDRoiobjectsOnly
	 *            if true, the iterable will contains only visible ThreeDRoiobjects of the
	 *            specified Time. Otherwise, it will contain all the ThreeDRoiobjects of
	 *            the specified Time.
	 * @param Time
	 *            the Time of the content the returned iterable will wrap.
	 * @return an iterable view of the content of a single Time of this Embryoobject
	 *         collection.
	 */
	public Iterable< Embryoobject > iterable( final int Time, final boolean visibleThreeDRoiobjectsOnly )
	{
		if ( visibleThreeDRoiobjectsOnly )
			return new TimeVisibleIterable( Time );

		return content.get( Time );
	}

	/*
	 * SORTEDMAP
	 */

	/**
	 * Stores the specified ThreeDRoiobjects as the content of the specified Time. The
	 * added ThreeDRoiobjects are all marked as not visible. Their {@link Embryoobject#Time} is
	 * updated to be the specified Time.
	 *
	 * @param Time
	 *            the Time to store these ThreeDRoiobjects at. The specified ThreeDRoiobjects replace
	 *            the previous content of this Time, if any.
	 * @param ThreeDRoiobjects
	 *            the ThreeDRoiobjects to store.
	 */
	public void put( final String Time, final Collection< Embryoobject > ThreeDRoiobjects )
	{
		final Set< Embryoobject > value = new HashSet< >( ThreeDRoiobjects );
		for ( final Embryoobject Embryoobject : value )
		{
			Embryoobject.putFeature( Embryoobject.POSITION_T, Double.valueOf( Time ) );
			Embryoobject.putFeature( VISIBLITY, ZERO );
		}
		content.put( Time, value );
	}

	/**
	 * Returns the first (lowest) Time currently in this collection.
	 *
	 * @return the first (lowest) Time currently in this collection.
	 */
	public String firstKey()
	{
		if ( content.isEmpty() ) { return Integer.toString(0); }
		return content.firstKey();
	}

	/**
	 * Returns the last (highest) Time currently in this collection.
	 *
	 * @return the last (highest) Time currently in this collection.
	 */
	public String lastKey()
	{
		if ( content.isEmpty() ) { return Integer.toString(0); }
		return content.lastKey();
	}

	/**
	 * Returns a NavigableSet view of the Times contained in this collection.
	 * The set's iterator returns the keys in ascending order. The set is backed
	 * by the map, so changes to the map are reflected in the set, and
	 * vice-versa. The set supports element removal, which removes the
	 * corresponding mapping from the map, via the Iterator.remove, Set.remove,
	 * removeAll, retainAll, and clear operations. It does not support the add
	 * or addAll operations.
	 * <p>
	 * The view's iterator is a "weakly consistent" iterator that will never
	 * throw ConcurrentModificationException, and guarantees to traverse
	 * elements as they existed upon construction of the iterator, and may (but
	 * is not guaranteed to) reflect any modifications subsequent to
	 * construction.
	 *
	 * @return a navigable set view of the Times in this collection.
	 */
	public NavigableSet< String > keySet()
	{
		return content.keySet();
	}

	/**
	 * Removes all the content from this collection.
	 */
	public void clear()
	{
		content.clear();
	}

	/*
	 * MULTITHREADING
	 */

	@Override
	public void setNumThreads()
	{
		this.numThreads = Runtime.getRuntime().availableProcessors();
	}

	@Override
	public void setNumThreads( final int numThreads )
	{
		this.numThreads = numThreads;
	}

	@Override
	public int getNumThreads()
	{
		return numThreads;
	}

	/*
	 * PRIVATE CLASSES
	 */

	private class AllThreeDRoiobjectsIterator implements Iterator< Embryoobject >
	{

		private boolean hasNext = true;

		private final Iterator< String > TimeIterator;

		private Iterator< Embryoobject > contentIterator;

		private Embryoobject next = null;

		public AllThreeDRoiobjectsIterator()
		{
			this.TimeIterator = content.keySet().iterator();
			if ( !TimeIterator.hasNext() )
			{
				hasNext = false;
				return;
			}
			final Set< Embryoobject > currentTimeContent = content.get( TimeIterator.next() );
			contentIterator = currentTimeContent.iterator();
			iterate();
		}

		private void iterate()
		{
			while ( true )
			{

				// Is there still ThreeDRoiobjects in current content?
				if ( !contentIterator.hasNext() )
				{
					// No. Then move to next Time.
					// Is there still Times to iterate over?
					if ( !TimeIterator.hasNext() )
					{
						// No. Then we are done
						hasNext = false;
						next = null;
						return;
					}
					
					contentIterator = content.get( TimeIterator.next() ).iterator();
					continue;
				}
				next = contentIterator.next();
				return;
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public Embryoobject next()
		{
			final Embryoobject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Remove operation is not supported for ThreeDRoiobjectCollection iterators." );
		}

	}

	private class VisibleThreeDRoiobjectsIterator implements Iterator< Embryoobject >
	{

		private boolean hasNext = true;

		private final Iterator< String > TimeIterator;

		private Iterator< Embryoobject > contentIterator;

		private Embryoobject next = null;

		private Set< Embryoobject > currentTimeContent;

		public VisibleThreeDRoiobjectsIterator()
		{
			this.TimeIterator = content.keySet().iterator();
			if ( !TimeIterator.hasNext() )
			{
				hasNext = false;
				return;
			}
			currentTimeContent = content.get( TimeIterator.next() );
			contentIterator = currentTimeContent.iterator();
			iterate();
		}

		private void iterate()
		{

			while ( true )
			{
				// Is there still ThreeDRoiobjects in current content?
				if ( !contentIterator.hasNext() )
				{
					// No. Then move to next Time.
					// Is there still Times to iterate over?
					if ( !TimeIterator.hasNext() )
					{
						// No. Then we are done
						hasNext = false;
						next = null;
						return;
					}
					
					// Yes. Then start iterating over the next Time.
					currentTimeContent = content.get( TimeIterator.next() );
					contentIterator = currentTimeContent.iterator();
					continue;
				}
				next = contentIterator.next();
				// Is it visible?
				if ( next.getFeature( VISIBLITY ).compareTo( ZERO ) > 0 )
				{
					// Yes! Be happy and return
					return;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public Embryoobject next()
		{
			final Embryoobject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Remove operation is not supported for ThreeDRoiobjectCollection iterators." );
		}

	}

	private class VisibleThreeDRoiobjectsTimeIterator implements Iterator< Embryoobject >
	{

		private boolean hasNext = true;

		private Embryoobject next = null;

		private final Iterator< Embryoobject > contentIterator;

		public VisibleThreeDRoiobjectsTimeIterator( final Set< Embryoobject > TimeContent )
		{
			if ( null == TimeContent )
			{
				this.contentIterator = EMPTY_ITERATOR;
			}
			else
			{
				this.contentIterator = TimeContent.iterator();
			}
			iterate();
		}

		private void iterate()
		{
			while ( true )
			{
				if ( !contentIterator.hasNext() )
				{
					// No. Then we are done
					hasNext = false;
					next = null;
					return;
				}
				next = contentIterator.next();
				// Is it visible?
				if ( next.getFeature( VISIBLITY ).compareTo( ZERO ) > 0 )
				{
					// Yes. Be happy, and return.
					return;
				}
			}
		}

		@Override
		public boolean hasNext()
		{
			return hasNext;
		}

		@Override
		public Embryoobject next()
		{
			final Embryoobject toReturn = next;
			iterate();
			return toReturn;
		}

		@Override
		public void remove()
		{
			throw new UnsupportedOperationException( "Remove operation is not supported for ThreeDRoiobjectCollection iterators." );
		}

	}

	/**
	 * Returns a new {@link ThreeDRoiobjectCollection}, made of only the ThreeDRoiobjects marked as
	 * visible. All the ThreeDRoiobjects will then be marked as not-visible.
	 *
	 * @return a new Embryoobject collection, made of only the ThreeDRoiobjects marked as visible.
	 */
	public EmbryoobjectCollection crop()
	{
		final EmbryoobjectCollection ns = new EmbryoobjectCollection();
		ns.setNumThreads( numThreads );

		final Collection< String > Times = content.keySet();
		final ExecutorService executors = Executors.newFixedThreadPool( numThreads );
		for ( final String Time : Times )
		{

			final Runnable command = new Runnable()
			{
				@Override
				public void run()
				{
					final Set< Embryoobject > fc = content.get( Time );
					final Set< Embryoobject > nfc = new HashSet< >( getNThreeDRoiobjects( Time, true ) );

					for ( final Embryoobject Embryoobject : fc )
					{
						if ( Embryoobject.getFeature( VISIBLITY ).compareTo( ZERO ) > 0 )
						{
							nfc.add( Embryoobject );
							Embryoobject.putFeature( VISIBLITY, ZERO );
						}
					}
					ns.content.put( Time, nfc );
				}
			};
			executors.execute( command );
		}

		executors.shutdown();
		try
		{
			final boolean ok = executors.awaitTermination( TIME_OUT_DELAY, TIME_OUT_UNITS );
			if ( !ok )
			{
				System.err.println( "[ThreeDRoiobjectCollection.crop()] Timeout of " + TIME_OUT_DELAY + " " + TIME_OUT_UNITS + " reached while cropping." );
			}
		}
		catch ( final InterruptedException e )
		{
			e.printStackTrace();
		}
		return ns;
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this Embryoobject
	 * collection.
	 */
	private final class WholeCollectionIterable implements Iterable< Embryoobject >
	{

		private final boolean visibleThreeDRoiobjectsOnly;

		public WholeCollectionIterable( final boolean visibleThreeDRoiobjectsOnly )
		{
			this.visibleThreeDRoiobjectsOnly = visibleThreeDRoiobjectsOnly;
		}

		@Override
		public Iterator< Embryoobject > iterator()
		{
			if ( visibleThreeDRoiobjectsOnly )
				return new VisibleThreeDRoiobjectsIterator();

			return new AllThreeDRoiobjectsIterator();
		}
	}

	/**
	 * A convenience wrapper that implements {@link Iterable} for this Embryoobject
	 * collection.
	 */
	private final class TimeVisibleIterable implements Iterable< Embryoobject >
	{

		private final int Time;

		public TimeVisibleIterable( final int Time )
		{
			this.Time = Time;
		}

		@Override
		public Iterator< Embryoobject > iterator()
		{
			return new VisibleThreeDRoiobjectsTimeIterator( content.get( Time ) );
		}
	}

	private static final Iterator< Embryoobject > EMPTY_ITERATOR = new Iterator< Embryoobject >()
	{

		@Override
		public boolean hasNext()
		{
			return false;
		}

		@Override
		public Embryoobject next()
		{
			return null;
		}

		@Override
		public void remove()
		{}
	};

	/*
	 * STATIC METHODS
	 */

	/**
	 * Creates a new {@link ThreeDRoiobjectCollection} containing only the specified ThreeDRoiobjects.
	 * Their Time origin is retrieved from their {@link Embryoobject#Time} feature, so
	 * it must be set properly for all ThreeDRoiobjects. All the ThreeDRoiobjects of the new
	 * collection have the same visibility that the one they carry.
	 *
	 * @param ThreeDRoiobjects
	 *            the Embryoobject collection to build from.
	 * @return a new {@link ThreeDRoiobjectCollection} instance.
	 */
	public static EmbryoobjectCollection fromCollection( final Iterable< Embryoobject > ThreeDRoiobjects )
	{
		final EmbryoobjectCollection sc = new EmbryoobjectCollection();
		for ( final Embryoobject Embryoobject : ThreeDRoiobjects )
		{
			final String Time = Double.toString(Embryoobject.getFeature( Embryoobject.POSITION_T )) ;
			Set< Embryoobject > fc = sc.content.get( Time );
			if ( null == fc )
			{
				fc = new HashSet< >();
				sc.content.put( Time, fc );
			}
			fc.add( Embryoobject );
		}
		return sc;
	}

	/**
	 * Creates a new {@link ThreeDRoiobjectCollection} from a copy of the specified map of
	 * sets. The ThreeDRoiobjects added this way are completely untouched. In particular,
	 * their {@link #VISIBLITY} feature is left untouched, which makes this
	 * method suitable to de-serialize a {@link ThreeDRoiobjectCollection}.
	 *
	 * @param source
	 *            the map to buidl the Embryoobject collection from.
	 * @return a new ThreeDRoiobjectCollection.
	 */
	public static EmbryoobjectCollection fromMap( final Map< String, Set< Embryoobject > > source )
	{
		final EmbryoobjectCollection sc = new EmbryoobjectCollection();
		sc.content = new ConcurrentSkipListMap< >( source );
		return sc;
	}
}
