package pl.polsl.blissapp.data.model;

import junit.framework.TestCase;

import java.util.List;

public class RadicalTest extends TestCase
{
    public void testGetChildren()
    {
        for (Radical radical : Radical.values())
        {
            if (radical.getParent() == null)
            {
                List<Radical> children = Radical.getChildren(radical);

                // Are all my children mine?
                for (Radical child : children)
                {
                    assertSame(child.getParent(), radical);
                }
            }
            else
            {
                // Does my parent know I am their child?
                assertTrue(Radical.getChildren(radical.getParent()).contains(radical));
            }
        }
    }
}