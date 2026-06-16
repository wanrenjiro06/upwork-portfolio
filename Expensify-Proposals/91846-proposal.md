# Proposal: Issue #91846 — Investigate SearchAutocompleteList Causing 2x Slowdown During Message Sending

## GitHub Comment (copy-paste exactly)

---

**Proposal**

**Problem:**
When a user navigates to a chat via Search (rather than Inbox), `SearchAutocompleteList` remains mounted in the background. When the user sends a message, `SearchAutocompleteList` re-renders because it subscribes to `useFilteredOptions`, which reacts to Onyx state changes triggered by the message send. This background re-render consumes most of the `ManualSendMessage` operation time, causing a ~2x slowdown.

**Root Cause:**
`useFilteredOptions` subscribes to:
- `ONYXKEYS.COLLECTION.REPORT`
- `ONYXKEYS.PERSONAL_DETAILS_LIST`
- `ONYXKEYS.COLLECTION.POLICY`

When a message is sent, Onyx updates `COLLECTION.REPORT`, which triggers `useFilteredOptions` to recompute the entire options list — even though `SearchAutocompleteList` is not visible to the user.

**Solution — Lazy mount (Option 3 from the issue):**
Only mount `SearchAutocompleteList` when the search input is focused. When the user is in a chat (not actively searching), the component should be unmounted entirely so it cannot react to Onyx updates.

```tsx
// In the parent Search router/component that renders SearchAutocompleteList
const [isSearchFocused, setIsSearchFocused] = useState(false);

// Only mount when search is active
{isSearchFocused && (
    <SearchAutocompleteList
        autocompleteQueryValue={queryValue}
        // ...other props
    />
)}

// Pass focus/blur handlers to the search input
<TextInput
    onFocus={() => setIsSearchFocused(true)}
    onBlur={() => setIsSearchFocused(false)}
    // ...other props
/>
```

**Why lazy mount over the other options:**
- **Option 1 (defer rendering)** — Still keeps the component subscribed to Onyx, just delays the paint. Doesn't fix the root cause.
- **Option 2 (stabilize inputs)** — Partial fix; `COLLECTION.REPORT` updates are legitimate triggers, hard to fully prevent without masking real updates.
- **Option 3 (lazy mount)** — Completely eliminates the background subscription. Zero cost when not searching. Clean and safe.

**Files to modify:**
- The parent component rendering `SearchAutocompleteList` (Search router/page component)
- Add `isSearchFocused` state and conditional mount
- Pass `onFocus`/`onBlur` to the search `TextInput`

**Performance impact:**
- `ManualSendMessage` slowdown eliminated when navigating via Search
- No regression when actively searching (component mounts on focus as before)
- Reduced background Onyx subscription overhead

**Testing:**
1. Navigate to a chat via Search
2. Send a message — measure render time (should be ~same as navigating via Inbox)
3. Tap the search bar → `SearchAutocompleteList` mounts and works as expected
4. Blur search → component unmounts
5. Send another message → no slowdown

---

**Expensify Email:** wanrenjiro06@gmail.com
**Upwork Profile:** https://www.upwork.com/freelancers/~0105d5114cf3ae419f?mp_source=share
