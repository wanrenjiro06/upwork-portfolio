# Proposal: Issue #93531 — Android Unread: Skeleton Loader Displayed After Receiving Message While on 'Unread'

## GitHub Comment (copy-paste exactly)

---

**Proposal**

**Problem:**
When a user is viewing the "Unread" filter in the LHN (Left Hand Navigation) and a new message arrives, a skeleton loader and empty state screen briefly flash before the new chat appears. This causes jarring UI flickering that should not happen — the new chat should appear directly.

**Root Cause:**
When a new message arrives, the Onyx `REPORT` collection updates, which triggers a re-render of the LHN. The unread filter re-evaluates its data, and during the brief moment between the old data clearing and the new data populating, the list shows zero items — which triggers the skeleton loader and empty state.

The issue is in how the filtered unread list handles the transition: it momentarily returns an empty array before the new report is included, causing the empty/skeleton states to render.

**Solution:**
Keep the previous list data visible during transitions instead of showing empty/skeleton states. Specifically:

1. **Hold the previous data** while new Onyx data is loading into the unread filter — similar to React Query's `keepPreviousData` pattern:

```tsx
// In the component that filters LHN data for the Unread tab
const previousDataRef = useRef(filteredReports);

const displayData = useMemo(() => {
    if (filteredReports.length === 0 && previousDataRef.current.length > 0) {
        // Don't flash empty — keep previous data until new data arrives
        return previousDataRef.current;
    }
    previousDataRef.current = filteredReports;
    return filteredReports;
}, [filteredReports]);
```

2. **Suppress skeleton/empty state** when data was previously populated — only show skeleton on initial load (when there was never any data), not on updates:

```tsx
const shouldShowSkeleton = isLoading && previousDataRef.current.length === 0;
const shouldShowEmptyState = !isLoading && displayData.length === 0;
```

**Files to modify:**
- `src/components/LHNOptionsList/LHNOptionsList.tsx` — add previous data ref to prevent empty flash
- The parent component/hook that filters reports for the Unread tab — add transition guard

**Testing:**
1. Open app → go to Inbox → tap "Unread" filter
2. Have another device/account send a message to your account
3. Verify: new chat appears directly with NO skeleton loader or empty state flash
4. Test on Android (primary), iOS, and web

---

**Expensify Email:** wanrenjiro06@gmail.com
**Upwork Profile:** https://www.upwork.com/freelancers/~0105d5114cf3ae419f?mp_source=share
