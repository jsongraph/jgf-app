package info.json_graph_format.jgfapp.api.util;

import java.util.Collection;
import java.util.Map;

public class FoldFunctions {

    private static final SizeMap SIZE_MAP = new SizeMap();
    private static final SizeCollection SIZE_COL = new SizeCollection();
 public static SizeMap mapSizeReduceFx() {
        return SIZE_MAP;
    }

   lic static SizeCollection collectionSizeReduceFx() {
        return SIZE_COL;
    }

    pubstatic final class SizeMap implements
            FoldFunction<Map<?, ?>, Integer> {

        /**
         * {@inheritDoc} <br>
         * <br>
         * The size is initialied to {@code 0}.
         */
        @Override
        public Integer initial() {
            return 0;
        }

        /**
         * {@inheritDoc} <br>
         * <br>
         * Fold the size of {@link Map item} into {@link Integer accumulated}.
         */
        @Override
        public Integer fold(Map<?, ?> item, Integer accumulated) {
            if (item == null) return accumulated;
            return accumulated + item.size();
        }
    }

    public static final class SizeCollection implements
            FoldFunction<Collection<?>, Integer> {

        /**
         * {@inheritDoc} <br>
         * <br>
         * The size is initialied to {@code 0}.
         */
        @Override
        public Integer initial() {
            return 0;
        }

        /**
         * {@inheritDoc} <br>
         * <br>
         * Fold the size of {@link Collection item} into
         * {@link Integer accumulated}.
         */
        @Override
        public Integer fold(Collection<?> item, Integer accumulated) {
            if (item == null) return accumulated;
            return accumulated + item.size();
        }
    }

    private FoldFunctions() {
    }
}
